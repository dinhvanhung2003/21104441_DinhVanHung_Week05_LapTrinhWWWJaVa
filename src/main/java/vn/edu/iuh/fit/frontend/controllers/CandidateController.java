package vn.edu.iuh.fit.frontend.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.backend.enums.SkillLevelType;
import vn.edu.iuh.fit.backend.models.*;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.backend.repositories.CandidateSkillRepository;
import vn.edu.iuh.fit.backend.repositories.ExperienceRepository;
import vn.edu.iuh.fit.backend.services.*;
import vn.edu.iuh.fit.backend.services.CandidateService;
import com.neovisionaries.i18n.CountryCode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import vn.edu.iuh.fit.frontend.models.Candidate;
@SessionAttributes("accountId")
@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateServices;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private JobApplicationService jobApplicationService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ExperienceRepository experienceRepository;
    @Autowired
    private CandidateSkillRepository candidateSkillRepository;
    @Autowired
    private SkillService skillService;
    @Autowired
    private SkillLevelService skillLevelService;
    @Autowired
    private JobService jobService;
    // Hiển thị danh sách candidates không phân trang
    @GetMapping("/list")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateServices.findAll());
        return "candidates/candidates";
    }

    // Hiển thị danh sách candidates có phân trang
    @GetMapping("/candidates")
    public String showCandidateListPaging(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<vn.edu.iuh.fit.backend.models.Candidate> candidatePage = candidateServices.findAll(currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("candidatePage", candidatePage);

        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "candidates/candidates-paging";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable Long jobId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName(); // Get logged-in user's username
            jobApplicationService.applyForJob(jobId, username);
            redirectAttributes.addFlashAttribute("successMessage", "Job application submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to apply for the job: " + e.getMessage());
        }
        return "redirect:/jobs/" + jobId;// Redirect to the job list page
    }


    @GetMapping("/profile")
    public String showCandidateForm(Principal principal, HttpSession session, Model model) {
        // Lấy danh sách quốc gia
        List<String> countries = Arrays.stream(CountryCode.values())
                .map(CountryCode::getName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("countries", countries);

// Kiểm tra trạng thái cập nhật hồ sơ

        // Lấy thông tin tài khoản qua username
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lưu accountId vào session
        Long accountId = account.getId();
        session.setAttribute("accountId", accountId);
        // Kiểm tra trạng thái cập nhật hồ sơ
        boolean isProfileUpdated = account.getCandidate() != null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vn.edu.iuh.fit.backend.models.Candidate candidate = isProfileUpdated ? account.getCandidate() : new vn.edu.iuh.fit.backend.models.Candidate();
        String formattedDob = candidate.getDob() != null ? candidate.getDob().format(formatter) : "";
        model.addAttribute("formattedDob", formattedDob);
        // Gán trạng thái vào model
        model.addAttribute("isProfileUpdated", isProfileUpdated);
        // Định dạng ngày tháng cho từ ngày và đến ngày trong kinh nghiệm làm việc







        if (candidate.getExperiences() != null) {
            candidate.getExperiences().forEach(exp -> {
                if (exp.getFromDate() == null) {
                    exp.setFromDate(LocalDate.of(1970, 1, 1)); // Giá trị mặc định
                }
                if (exp.getToDate() == null) {
                    exp.setToDate(LocalDate.of(1970, 1, 1)); // Giá trị mặc định
                }
            });
        }

        // Chuẩn bị dữ liệu khác
        Candidate candidateFullInfoDTO = new Candidate();
        candidateFullInfoDTO.setCandidate(isProfileUpdated ? account.getCandidate() : new vn.edu.iuh.fit.backend.models.Candidate());
        candidateFullInfoDTO.setAddress(isProfileUpdated ? account.getCandidate().getAddress() : new Address());
        candidateFullInfoDTO.setExperiences(isProfileUpdated ? account.getCandidate().getExperiences() : Collections.singletonList(new Experience()));
        candidateFullInfoDTO.setCandidateSkills(isProfileUpdated ? account.getCandidate().getCandidateSkills() : Collections.singletonList(new CandidateSkill()));

        // Thêm các dữ liệu vào model
        model.addAttribute("candidateFullInfoDTO", candidateFullInfoDTO);
        model.addAttribute("skills", skillService.findAll());
        model.addAttribute("skillLevels", List.of(SkillLevelType.BEGINNER, SkillLevelType.INTERMEDIATE, SkillLevelType.EXPERT));

        return "candidates/candidate-info"; // Trả về view để hiển thị form
    }




    @PostMapping("/profile")
    public String saveOrUpdateCandidateProfile(
            @ModelAttribute Candidate candidateFullInfoDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // Lấy thông tin accountId từ session
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new RuntimeException("Account ID không tồn tại trong session!");
        }

        // Lấy thông tin tài khoản từ database
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Lấy hoặc tạo mới thông tin ứng viên
        vn.edu.iuh.fit.backend.models.Candidate candidate = account.getCandidate();
        if (candidate == null) {
            candidate = new vn.edu.iuh.fit.backend.models.Candidate();
        }

        // Cập nhật thông tin ứng viên từ DTO
        candidate.setFullName(candidateFullInfoDTO.getCandidate().getFullName());
        candidate.setDob(candidateFullInfoDTO.getCandidate().getDob());
        candidate.setEmail(candidateFullInfoDTO.getCandidate().getEmail());
        candidate.setPhone(candidateFullInfoDTO.getCandidate().getPhone());

        // Xử lý địa chỉ
        Address address = candidateFullInfoDTO.getAddress();
        Address savedAddress = addressRepository.save(address);
        candidate.setAddress(savedAddress);

        // Lưu thông tin ứng viên
        vn.edu.iuh.fit.backend.models.Candidate savedCandidate = candidateRepository.save(candidate);
        account.setCandidate(savedCandidate);
        accountRepository.save(account);

        // Xử lý danh sách kinh nghiệm
        List<Experience> experiences = candidateFullInfoDTO.getExperiences();
        experiences.forEach(exp -> exp.setCandidate(savedCandidate));
        experienceRepository.saveAll(experiences);

        // Xử lý danh sách kỹ năng
        List<CandidateSkill> candidateSkills = candidateFullInfoDTO.getCandidateSkills();
        candidateSkills.forEach(skill -> skill.setCandidate(savedCandidate));
        candidateSkillRepository.saveAll(candidateSkills);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Thông tin ứng viên đã được lưu thành công!");

        return "redirect:/candidates/dashboard"; // Chuyển hướng về trang dashboard
    }
    //trang dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal, HttpSession session) {
        // Lấy thông tin tài khoản từ session
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new RuntimeException("Account ID không tồn tại trong session!");
        }

        // Lấy thông tin tài khoản từ database
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Lấy danh sách công việc đã nộp
        List<Job> appliedJobs = jobService.findByCandidates(account.getCandidate());

        // Thêm thông tin vào model
        model.addAttribute("username", principal.getName());
        model.addAttribute("candidate", account.getCandidate());
        model.addAttribute("appliedJobs", appliedJobs);

        return "candidates/dashboard"; // Tên file view cho trang Dashboard
    }
    @GetMapping("/applied-jobs")
    public String showAppliedJobs(Model model, Principal principal, HttpSession session) {
        // Lấy thông tin tài khoản từ session
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new RuntimeException("Account ID không tồn tại trong session!");
        }
        // Lấy thông tin tài khoản từ database
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Lấy danh sách công việc đã ứng tuyển
        List<Job> appliedJobs = jobService.findByCandidates(account.getCandidate());

        // Thêm thông tin vào model
        model.addAttribute("appliedJobs", appliedJobs);
        return "candidates/candidate-jobs"; // Tên file view cho danh sách công việc đã ứng tuyển
    }

}
