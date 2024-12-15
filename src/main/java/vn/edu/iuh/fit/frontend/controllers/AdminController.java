package vn.edu.iuh.fit.frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.backend.models.Account;
import vn.edu.iuh.fit.backend.models.Role;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.repositories.RoleRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "admin/account-management";
    }

    @GetMapping("/accounts/add")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/add-account";
    }

    @PostMapping("/accounts/add")
    public String addAccount(@ModelAttribute Account account) {
        account.setEnabled(true); // Kích hoạt tài khoản mặc định
        accountRepository.save(account);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/accounts/edit/{id}")
    public String showEditAccountForm(@PathVariable Long id, Model model) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid account ID: " + id));
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/edit-account";
    }

    @PostMapping("/accounts/edit/{id}")
    public String editAccount(@PathVariable Long id, @ModelAttribute Account account) {
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid account ID: " + id));
        existingAccount.setUsername(account.getUsername());
        existingAccount.setRole(account.getRole());
        existingAccount.setEnabled(account.isEnabled());
        accountRepository.save(existingAccount);
        return "redirect:/admin/accounts";
    }

    @PostMapping("/accounts/disable/{id}")
    public String disableAccount(@PathVariable Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid account ID: " + id));
        account.setEnabled(false); // Vô hiệu hóa tài khoản
        accountRepository.save(account);
        return "redirect:/admin/accounts";
    }
}
