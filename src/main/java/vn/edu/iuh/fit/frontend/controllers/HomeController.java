package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.fit.backend.models.*;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.services.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private JobService jobService;
    @Autowired
    private CandidateSkillService candidateSkillService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobSkillService jobSkillService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RestTemplate restTemplate;
    @GetMapping("/home")
    public String viewHomePage(
            Model model,
            @RequestParam("query") Optional<String> query,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("sortField") Optional<String> sortField,
            @RequestParam("sortDir") Optional<String> sortDir,
            Principal principal) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String currentSortField = sortField.orElse("jobName");
        String currentSortDir = sortDir.orElse("asc");

        // Lấy thông tin ứng viên đã đăng nhập
        Account account = accountService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Candidate candidate = account.getCandidate();
        if (candidate == null) {
            return "redirect:/candidates/register/full-info?accountId=" + account.getId();
        }

        // Lấy kỹ năng của ứng viên
        List<String> candidateSkills = candidateSkillService.findSkillsByCandidateId(candidate.getId())
                .stream()
                .map(Skill::getSkillName)
                .collect(Collectors.toList());

        String skillsString = String.join(", ", candidateSkills);
        System.out.println("Candidate Skills: " + candidateSkills);

        // Gửi kỹ năng tới Flask API
        String apiUrl = "http://localhost:5000/recommend";
        Map<String, String> request = Map.of("candidate_skills", skillsString);

        ResponseEntity<List> response = restTemplate.postForEntity(apiUrl, request, List.class);
        List<Map<String, Object>> recommendations = (List<Map<String, Object>>) response.getBody();
        System.out.println("Recommendations: " + recommendations);
        // Xử lý danh sách công việc gợi ý
        List<Job> suggestedJobs = new ArrayList<>();
        if (recommendations != null) {
            for (Map<String, Object> rec : recommendations) {
                Long jobId = Long.valueOf(rec.get("job_id").toString());
                jobService.findJobById(jobId).ifPresent(suggestedJobs::add);
            }
        }
        model.addAttribute("suggestedJobs", suggestedJobs);

        // Xử lý tìm kiếm hoặc hiển thị tất cả công việc
        Page<Job> jobPage;
        String searchQuery = query.orElse("");
        if (!searchQuery.isEmpty()) {
            jobPage = jobService.searchJobs(searchQuery, currentPage - 1, pageSize);
        } else {
            jobPage = jobService.findAll(currentPage - 1, pageSize, currentSortField, currentSortDir);
        }

        // Đưa dữ liệu vào model
        model.addAttribute("jobPage", jobPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", currentSortField);
        model.addAttribute("sortDir", currentSortDir);
        model.addAttribute("reverseSortDir", currentSortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("query", searchQuery);

        // Phân trang
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "index.html";
    }



    @GetMapping("/jobs/{jobId}")
    public String getJobDetail(@PathVariable("jobId") Long jobId, Model model) {
        Optional<Job> jobOptional = jobService.findJobById(jobId);

        if (jobOptional.isEmpty()) {
            return "redirect:/home"; // Redirect nếu công việc không tồn tại
        }

        Job job = jobOptional.get(); // Lấy giá trị Job thực tế từ Optional

        // Lấy thông tin công ty của công việc
        Company company = companyService.findCompanyById(job.getCompany().getId());
        List<JobSkill> jobSkills = jobSkillService.findSkillsByJobId(jobId);

        model.addAttribute("job", job); // Đưa Job vào model
        model.addAttribute("company", company);
        model.addAttribute("jobSkills", jobSkills);

        return "jobs/details.html"; // Chuyển đến trang chi tiết công việc
    }

    @GetMapping("/home/check")
    public String home(Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Kiểm tra và chuyển hướng nếu thông tin Candidate chưa đầy đủ
        if (account.getCandidate() == null) {
            return "redirect:/candidates/register/full-info?accountId=" + account.getId();
        }

        return "home"; // Trang chủ nếu thông tin đã hoàn tất
    }



}
