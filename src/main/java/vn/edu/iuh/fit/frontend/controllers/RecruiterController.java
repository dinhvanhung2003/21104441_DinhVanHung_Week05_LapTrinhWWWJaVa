package vn.edu.iuh.fit.frontend.controllers;

import com.neovisionaries.i18n.CountryCode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.backend.enums.SkillLevelType;
import vn.edu.iuh.fit.backend.enums.SkillType;
import vn.edu.iuh.fit.backend.models.*;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.backend.services.*;
import vn.edu.iuh.fit.frontend.models.CompanyFullInfo;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private JobService jobService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailLogService emailLogService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private JobSkillService jobSkillService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CompanyRepository companyRepository;
    // Trang quản lý công việc
    @GetMapping("dashboard/jobs")
    public String listJobs(Model model, Principal principal,String search) {
        String recruiterUsername = principal.getName(); // Lấy username nhà tuyển dụng
        List<Job> jobs;
        if (search != null && !search.isEmpty()) {
            // Lấy danh sách công việc theo tên công việc
            jobs = jobService.findByJobNameContaining(search);
        } else {
            jobs = jobService.getJobsByRecruiterUsername(recruiterUsername);

        }
        model.addAttribute("jobs", jobs);
        return "recruiter/jobs";
    }
    @GetMapping("dashboard/jobs/{jobId}/candidates")
    public String listCandidates(@PathVariable Long jobId, Model model, Principal principal) {
        // Lấy thông tin công việc
        Optional<Job> job = jobService.findJobById(jobId);

        if (job.isEmpty()) {
            throw new IllegalArgumentException("Công việc không tồn tại");
        }

        // Lấy danh sách ứng viên cho công việc
        List<Candidate> candidates = jobService.getCandidatesByJobId(jobId);

        // Thêm dữ liệu vào model
        model.addAttribute("job", job.get());
        model.addAttribute("candidates", candidates);

        return "recruiter/job-candidates";
    }
//    @GetMapping("/{jobId}/candidates/{candidateId}/evaluate")
//    public String evaluateCandidate(
//            @PathVariable Long jobId,
//            @PathVariable Long candidateId,
//            Model model) {
//
//        // Đánh giá ứng viên và gửi email nếu phù hợp
//        String resultMessage = jobService.evaluateAndInvite(candidateId, jobId);
//
//        model.addAttribute("message", resultMessage);
//        return "recruiter/evaluation-result";
//    }
@PostMapping("/dashboard/jobs/{jobId}/candidates/evaluate-all")
public String evaluateAllCandidates(@PathVariable Long jobId, RedirectAttributes redirectAttributes) {
    try {
        // Gửi yêu cầu POST tới API Flask để đánh giá tất cả ứng viên
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("job_id", jobId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<List> response = restTemplate.exchange(
                "http://localhost:5000/evaluate", // Thay bằng API Flask của bạn
                HttpMethod.POST,
                request,
                List.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            List sentEmails = response.getBody();
            redirectAttributes.addFlashAttribute("successMessage", "Đánh giá thành công. Email đã được gửi!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể đánh giá ứng viên cho công việc này.");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi gọi API đánh giá ứng viên: " + e.getMessage());
    }
    return "redirect:/recruiter/dashboard/jobs/" + jobId + "/candidates/sent-emails";
}



    // Trang Dashboard của nhà tuyển dụng
    @GetMapping("/dashboard")
    public String viewDashboard(Model model, Principal principal) {
        String recruiterUsername = principal.getName(); // Lấy username của nhà tuyển dụng
//        Company company = accountService.getCompanyByRecruiterUsername(recruiterUsername); // Lấy thông tin công ty
        List<Job> jobs = jobService.getJobsByRecruiterUsername(recruiterUsername); // Lấy danh sách công việc

        // Đưa dữ liệu vào model
        model.addAttribute("recruiterUsername", recruiterUsername);
//        model.addAttribute("company", company);
        model.addAttribute("jobs", jobs);

        return "recruiter/dashboard"; // Trả về trang Dashboard
    }
    @GetMapping("/dashboard/jobs/{jobId}/candidates/sent-emails")
    public String viewSentEmails(@PathVariable Long jobId, Model model) {
        try {
            // Lấy danh sách email đã gửi từ cơ sở dữ liệu
            List<EmailLog> sentEmails = emailLogService.getEmailLogsByJobId(jobId);

            if (sentEmails.isEmpty()) {
                model.addAttribute("error", "Không có ứng viên nào đã được gửi email cho công việc này.");
            } else {
                model.addAttribute("sentEmails", sentEmails);
            }

            model.addAttribute("jobId", jobId);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi truy xuất danh sách email: " + e.getMessage());
        }

        return "recruiter/sentEmails";
    }
    @GetMapping("/dashboard/jobs/new")
    public String showJobForm(Model model, Principal principal) {
        Account account = accountService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản."));
        Company currentCompany = account.getCompany();
        Job job = new Job();
        job.setCompany(currentCompany);

        model.addAttribute("job", job);
        model.addAttribute("skillLevels", SkillLevelType.values());
        model.addAttribute("skillTypes", SkillType.values());
        return "recruiter/new-job";
    }
    @PostMapping("/dashboard/jobs/new")
    public String saveJob(
            Principal principal,
            @ModelAttribute Job job,
            @RequestParam("skillName[]") List<String> skillNames,
            @RequestParam("skillType[]") List<SkillType> skillTypes,
            @RequestParam("skillDesc[]") List<String> skillDescs,
            @RequestParam("skillLevel[]") List<Integer> skillLevels, // Changed to List<Integer>
            @RequestParam("moreInfos[]") List<String> moreInfos) {

        Account account = accountService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản."));
        Company currentCompany = account.getCompany();
        job.setCompany(currentCompany);
        jobService.saveJob(job);

        for (int i = 0; i < skillNames.size(); i++) {
            Skill skill = new Skill();
            skill.setSkillName(skillNames.get(i));
            skill.setSkillDescription(skillDescs.get(i));
            skill.setType(skillTypes.get(i));
            skillService.save(skill);

            JobSkill jobSkill = new JobSkill();
            JobSkillId jobSkillId = new JobSkillId(job.getId(), skill.getId());
            jobSkill.setId(jobSkillId);
            jobSkill.setJob(job);
            jobSkill.setSkill(skill);
            jobSkill.setSkillLevel(SkillLevelType.fromId(skillLevels.get(i))); // Convert Integer to Enum
            jobSkill.setMoreInfos(moreInfos.get(i));

            jobSkillService.save(jobSkill);
        }

        return "redirect:/dashboard/jobs";
    }



    @GetMapping("/dashboard/jobs/{jobId}/skills")
    public String viewSkills(@PathVariable Long jobId, Model model) {

        List<JobSkill> jobSkills = jobSkillService.findSkillsByJobId(jobId);
        model.addAttribute("jobSkills", jobSkills);
        return "recruiter/job-skills"; // Trả về template job-skills.html
    }

    @GetMapping("dashboard/info-company")
    public String showCompanyForm(Principal principal, HttpSession session, Model model) {
        // Lấy thông tin tài khoản qua username
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lưu accountId vào session
        Long accountId = account.getId();
        session.setAttribute("accountId", accountId);

        // Kiểm tra trạng thái cập nhật hồ sơ công ty
        boolean isProfileUpdated = account.getCompany() != null;
        Company company = isProfileUpdated ? account.getCompany() : new Company();

        // Chuẩn bị dữ liệu DTO cho form
        CompanyFullInfo companyFullInfoDTO = new CompanyFullInfo();
        companyFullInfoDTO.setCompany(company);
        companyFullInfoDTO.setAddress(company.getAddress() != null ? company.getAddress() : new Address());

        // Thêm danh sách CountryCode (giống Candidate)
        List<String> countries = Arrays.stream(CountryCode.values())
                .map(CountryCode::getName)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        // Gán trạng thái và danh sách vào model
        model.addAttribute("isProfileUpdated", isProfileUpdated);
        model.addAttribute("companyFullInfoDTO", companyFullInfoDTO);
        model.addAttribute("countries", countries);

        return "recruiter/info-company"; // View hiển thị form thông tin công ty
    }


    @PostMapping("/dashboard/info-company")
    public String saveOrUpdateCompanyProfile(
            @ModelAttribute CompanyFullInfo companyFullInfoDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy account ID từ session
            Long accountId = (Long) session.getAttribute("accountId");
            if (accountId == null) {
                throw new RuntimeException("Không tìm thấy tài khoản trong session!");
            }

            // Lấy tài khoản từ database
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

            // Lấy công ty hoặc tạo mới
            Company company = account.getCompany();
            if (company == null) {
                company = new Company();
            }

            // Lấy dữ liệu công ty từ DTO
            Company companyData = companyFullInfoDTO.getCompany();
            if (companyData == null || companyData.getCompName() == null || companyData.getCompName().isEmpty()) {
                throw new RuntimeException("Tên công ty không được để trống!");
            }

            company.setCompName(companyData.getCompName());
            company.setAbout(companyData.getAbout());
            company.setPhone(companyData.getPhone());
            company.setEmail(companyData.getEmail());
            company.setWebUrl(companyData.getWebUrl());

            // Xử lý địa chỉ
            Address address = companyFullInfoDTO.getAddress();
            if (address == null) {
                address = new Address(); // Tạo mới nếu không có địa chỉ
            }
            Address savedAddress = addressRepository.save(address);
            company.setAddress(savedAddress);

            // Lưu công ty
            Company savedCompany = companyRepository.save(company);

            // Liên kết với tài khoản
            account.setCompany(savedCompany);
            accountRepository.save(account);

            // Gửi thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Thông tin công ty đã được cập nhật thành công!");
            return "redirect:/recruiter/dashboard/info-company";
        } catch (Exception e) {
            // Gửi thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật thông tin công ty: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/recruiter/dashboard/info-company";
        }
    }


}
