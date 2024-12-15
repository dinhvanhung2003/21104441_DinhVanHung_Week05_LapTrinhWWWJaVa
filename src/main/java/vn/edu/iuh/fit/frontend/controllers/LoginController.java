package vn.edu.iuh.fit.frontend.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.services.AccountService;

@Controller
public class LoginController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Tên của file login.html trong thư mục templates
    }
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa session
        session.invalidate();

        // Thêm thông báo hoặc chuyển hướng đến trang đăng nhập
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công!");
        return "redirect:/login"; // Chuyển hướng về trang đăng nhập
    }


}

