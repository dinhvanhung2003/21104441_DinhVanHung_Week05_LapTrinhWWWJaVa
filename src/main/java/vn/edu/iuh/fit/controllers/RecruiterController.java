package vn.edu.iuh.fit.controllers;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.models.Company;
import vn.edu.iuh.fit.models.EmailLog;
import vn.edu.iuh.fit.models.Job;
import vn.edu.iuh.fit.models.Candidate;
import vn.edu.iuh.fit.services.CandidateService;
import vn.edu.iuh.fit.services.EmailLogService;
import vn.edu.iuh.fit.services.JobService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    // Trang quản lý công việc
    @GetMapping("dashboard/jobs")
    public String listJobs(Model model, Principal principal) {
        String recruiterUsername = principal.getName(); // Lấy username nhà tuyển dụng
        List<Job> jobs = jobService.getJobsByRecruiterUsername(recruiterUsername); // Lấy công việc theo username
        model.addAttribute("jobs", jobs);
        return "recruiter/jobs";
    }

    // Trang thêm job
    @GetMapping("/new")
    public String newJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "recruiter/new-job";
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
public String evaluateAllCandidates(@PathVariable Long jobId, Model model) {
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
            List<Map<String, Object>> sentEmails = response.getBody();
            model.addAttribute("sentEmails", sentEmails);
            model.addAttribute("jobId", jobId);
        } else {
            model.addAttribute("error", "Không thể đánh giá ứng viên cho công việc này.");
        }
    } catch (Exception e) {
        model.addAttribute("error", "Lỗi khi gọi API đánh giá ứng viên: " + e.getMessage());
    }
    return "recruiter/sentEmails";
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


}
