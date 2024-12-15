package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.iuh.fit.backend.models.Account;
import vn.edu.iuh.fit.backend.models.Candidate;
import vn.edu.iuh.fit.backend.models.Role;
import vn.edu.iuh.fit.backend.repositories.RoleRepository;
import vn.edu.iuh.fit.backend.services.AccountService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    @Autowired
    private RoleRepository roleService;
    @Autowired
    private AccountService accountService;
    // Hiển thị form đăng ký tài khoản
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("candidate", new Candidate());
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerAccount(@ModelAttribute Account account,
                                  @RequestParam(required = false) boolean isRecruiter,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model) {
        try {
            // Check if passwords match
            if (!account.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp");
                return "register";
            }

            // Check if username already exists
            if (accountService.findByUsername(account.getUsername()).isPresent()) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại");
                return "register";
            }

            // Assign the appropriate role
            Role role;
            if (isRecruiter) {
                role = roleService.findByRoleName("ROLE_RECRUITER")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
            } else {
                role = roleService.findByRoleName("ROLE_CANDIDATE")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
            }
            account.setRole(role);
            account.setEnabled(true);

            // Save the account
            accountService.saveAccount(account);

            // Redirect to login page
            return "redirect:/login?registerSuccess=true";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }

}

