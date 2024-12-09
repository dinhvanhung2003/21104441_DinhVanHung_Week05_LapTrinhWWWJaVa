package vn.edu.iuh.fit.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.enums.SkillLevelType;
import vn.edu.iuh.fit.models.*;
import vn.edu.iuh.fit.repositories.AccountRepository;
import vn.edu.iuh.fit.repositories.AddressRepository;
import vn.edu.iuh.fit.repositories.CandidateRepository;
import vn.edu.iuh.fit.repositories.ExperienceRepository;
import vn.edu.iuh.fit.services.AccountService;
import vn.edu.iuh.fit.services.CandidateService;
import vn.edu.iuh.fit.services.JobApplicationService;
import vn.edu.iuh.fit.services.RoleService;
import com.neovisionaries.i18n.CountryCode;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import vn.edu.iuh.fit.dtos.CandidateFullInfoDTO;
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
    private vn.edu.iuh.fit.repositories.CandidateSkillRepository candidateSkillRepository;
    @Autowired
    private vn.edu.iuh.fit.services.SkillService skillService;
    @Autowired
    private vn.edu.iuh.fit.services.SkillLevelService skillLevelService;

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

        Page<Candidate> candidatePage = candidateServices.findAll(currentPage - 1, pageSize, "id", "asc");

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



    //dang ky tai khoan va thong tin ca nhan

//    @GetMapping("/register/full-info")
//    public String showCandidateForm(Principal principal, HttpSession session, Model model) {
//
//        List<String> countries = Arrays.stream(CountryCode.values())
//                .map(CountryCode::getName) // Lấy tên quốc gia
//                .filter(name -> name != null) // Loại bỏ các giá trị null
//                .sorted() // Sắp xếp theo thứ tự bảng chữ cái
//                .collect(Collectors.toList());
//
//        model.addAttribute("countries", countries);
//
//        Account account = accountRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new RuntimeException("Account not found"));
//
//        // Lưu accountId vào session để sử dụng sau
//        session.setAttribute("accountId", account.getId());
//
//        // Kiểm tra nếu Candidate đã hoàn tất thông tin, chuyển hướng về trang chủ
//        if (account.getCandidate() != null) {
//            return "redirect:/home"; // Chuyển hướng nếu tài khoản đã có thông tin Candidate
//        }
//
//        Candidate candidate = new Candidate();
//        candidate.setExperiences(List.of(new Experience())); // Tạo ít nhất một Experience
//        candidate.setCandidateSkills(List.of(new CandidateSkill()));
//        model.addAttribute("candidate", candidate);
//        model.addAttribute("skills", skillService.findAll());
//        model.addAttribute("skillLevels", List.of(SkillLevelType.BEGINNER, SkillLevelType.INTERMEDIATE, SkillLevelType.EXPERT));
//        return "candidates/candidate-info";
//    }
//
//
//
//    // Lưu thông tin Candidate
//    @PostMapping("/register/full-info")
//    public String registerFullInfo(@ModelAttribute CandidateFullInfoDTO candidateFullInfoDTO, HttpSession session) {
//        // Lấy accountId từ session
//        Long accountId = (Long) session.getAttribute("accountId");
//        if (accountId == null) {
//            throw new RuntimeException("Session không chứa thông tin tài khoản!");
//        }
//
//        // Tìm tài khoản đã đăng nhập
//        Account account = accountRepository.findById(accountId)
//                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
//
//        // Lưu thông tin Address
//        Address address = candidateFullInfoDTO.getAddress();
//        Address savedAddress = addressRepository.save(address);
//
//        // Lưu thông tin Candidate
//        Candidate candidate = candidateFullInfoDTO.getCandidate();
//
//        candidate.setAddress(savedAddress);
//        Candidate savedCandidate = candidateRepository.save(candidate);
//
//        // Gán candidate vào tài khoản
//        account.setCandidate(savedCandidate);
//        accountRepository.save(account);
//
//        // Lưu thông tin Experience
//        List<Experience> experiences = candidateFullInfoDTO.getExperiences();
//        experiences.forEach(exp -> exp.setCandidate(savedCandidate));
//        experienceRepository.saveAll(experiences);
//
//        // Lưu thông tin CandidateSkill
//        List<CandidateSkill> candidateSkills = candidateFullInfoDTO.getCandidateSkills();
//        candidateSkills.forEach(skill -> skill.setCandidate(savedCandidate));
//        candidateSkillRepository.saveAll(candidateSkills);
//        System.out.println("Candidate DOB: " + candidate.getDob());
//        return "redirect:/home"; // Chuyển hướng về trang chủ
//    }
// Hiển thị form đăng ký thông tin cá nhân
@GetMapping("/register/full-info")
public String showCandidateForm(Principal principal, HttpSession session, Model model) {
    List<String> countries = Arrays.stream(CountryCode.values())
            .map(CountryCode::getName)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());

    model.addAttribute("countries", countries);

    Account account = accountRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Account not found"));

    session.setAttribute("accountId", account.getId());

    if (account.getCandidate() != null) {
        return "redirect:/home";
    }

    CandidateFullInfoDTO candidateFullInfoDTO = new CandidateFullInfoDTO();
    candidateFullInfoDTO.setCandidate(new Candidate());
    candidateFullInfoDTO.setAddress(new Address());
    candidateFullInfoDTO.setExperiences(Collections.singletonList(new Experience()));
    candidateFullInfoDTO.setCandidateSkills(Collections.singletonList(new CandidateSkill()));

    model.addAttribute("candidateFullInfoDTO", candidateFullInfoDTO);
    model.addAttribute("skills", skillService.findAll());
    model.addAttribute("skillLevels", List.of(SkillLevelType.BEGINNER, SkillLevelType.INTERMEDIATE, SkillLevelType.EXPERT));
    return "candidates/candidate-info";
}

    // Xử lý lưu thông tin cá nhân
    @PostMapping("/register/full-info")
    public String registerFullInfo(@ModelAttribute CandidateFullInfoDTO candidateFullInfoDTO, HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
        if (accountId == null) {
            throw new RuntimeException("Session không chứa thông tin tài khoản!");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        Address address = candidateFullInfoDTO.getAddress();
        Address savedAddress = addressRepository.save(address);

        Candidate candidate = candidateFullInfoDTO.getCandidate();
        if (candidate == null) {
            throw new RuntimeException("Candidate không được null!");
        }

        candidate.setAddress(savedAddress);
        Candidate savedCandidate = candidateRepository.save(candidate);

        account.setCandidate(savedCandidate);
        accountRepository.save(account);

        List<Experience> experiences = candidateFullInfoDTO.getExperiences();
        experiences.forEach(exp -> exp.setCandidate(savedCandidate));
        experienceRepository.saveAll(experiences);

        List<CandidateSkill> candidateSkills = candidateFullInfoDTO.getCandidateSkills();
        candidateSkills.forEach(skill -> skill.setCandidate(savedCandidate));
        candidateSkillRepository.saveAll(candidateSkills);

        return "redirect:/home";
    }

}
