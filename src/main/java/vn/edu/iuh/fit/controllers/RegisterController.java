package vn.edu.iuh.fit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.iuh.fit.models.Account;
import vn.edu.iuh.fit.models.Candidate;
import vn.edu.iuh.fit.models.Role;
import vn.edu.iuh.fit.repositories.AccountRepository;
import vn.edu.iuh.fit.repositories.RoleRepository;
import vn.edu.iuh.fit.services.AccountService;

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
    public String registerAccount(@ModelAttribute Account account, Model model) {
        try {
            // Kiểm tra nếu username đã tồn tại
            if (accountService.findByUsername(account.getUsername()) != null) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại");
                return "register";
            }

            // Gán ROLE_CANDIDATE mặc định
            Role roleCandidate = roleService.findByRoleName("ROLE_CANDIDATE")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            account.setRole(roleCandidate);
            account.setEnabled(true);
            // Lưu tài khoản thông qua AccountService
            Account savedAccount = accountService.saveAccount(account);


            // Chuyển hướng đến trang đăng nhập
            return "redirect:/login?registerSuccess=true"; // Đăng nhập trước khi cập nhật thông tin
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }
}

