package vn.edu.iuh.fit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.models.Account;
import vn.edu.iuh.fit.models.Company;
import vn.edu.iuh.fit.models.Job;
import vn.edu.iuh.fit.models.JobSkill;
import vn.edu.iuh.fit.repositories.AccountRepository;
import vn.edu.iuh.fit.services.CompanyService;
import vn.edu.iuh.fit.services.JobService;
import vn.edu.iuh.fit.services.JobSkillService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {

    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobSkillService jobSkillService;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/home")
    public String viewHomePage(Model model,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size,
                               @RequestParam("sortField") Optional<String> sortField,
                               @RequestParam("sortDir") Optional<String> sortDir) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String currentSortField = sortField.orElse("jobName");
        String currentSortDir = sortDir.orElse("asc");

        // Fetch data
        Page<Job> jobPage = jobService.findAll(currentPage - 1, pageSize, currentSortField, currentSortDir);
        model.addAttribute("jobPage", jobPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", currentSortField);
        model.addAttribute("sortDir", currentSortDir);
        model.addAttribute("reverseSortDir", currentSortDir.equals("asc") ? "desc" : "asc");

        // Generate pagination numbers
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        System.out.println("Total Pages: " + jobPage.getTotalPages());
        System.out.println("Total Elements: " + jobPage.getTotalElements());

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
    @GetMapping("/search")
    public String searchJobs(@RequestParam("query") String query,
                             Model model,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        // Call service to get search results
        Page<Job> jobPage = jobService.searchJobs(query, currentPage - 1, pageSize);

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("query", query);

        // Pagination numbers
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "search-results"; // Returns a search result page
    }


}
