package vn.edu.iuh.fit.controllers;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.models.Company;
import vn.edu.iuh.fit.models.Job;
import vn.edu.iuh.fit.models.Candidate;
import vn.edu.iuh.fit.services.CandidateService;
import vn.edu.iuh.fit.services.JobService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private JobService jobService;
    @Autowired
    private CandidateService candidateService;

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
@PostMapping("/{jobId}/candidates/evaluate-all")
public String evaluateAllCandidates(@PathVariable Long jobId, RedirectAttributes redirectAttributes,Model model) throws MessagingException {
    // Đánh giá tất cả ứng viên cho công việc
//    String resultMessage = jobService.evaluateAndInviteAll(jobId);
    List<Candidate> invitedCandidates = jobService.evaluateCandidatesAndSendInvites(jobId);
    model.addAttribute("invitedCandidates", invitedCandidates);
//    redirectAttributes.addFlashAttribute("message", resultMessage);
    return "recruiter/evaluation-result";
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
}
