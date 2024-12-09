package vn.edu.iuh.fit.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.models.Account;
import vn.edu.iuh.fit.repositories.AccountRepository;

import java.util.Optional;
@Service
public class UserService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Tìm kiếm user với username: " + username);

        return accountRepository.findByUsername(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> {
                    System.out.println("Không tìm thấy user: " + username);
                    return new UsernameNotFoundException("User not found");
                });
    }


    private UserDetails mapToUserDetails(Account account) {
        System.out.println("Chuyển đổi Account sang UserDetails: " + account);

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword()) // Mật khẩu đã mã hóa
                .roles(account.getRole().getRoleName().replace("ROLE_", "")) // Loại bỏ tiền tố "ROLE_"
                .build();
    }

    public Optional<UserDetails> findByUsername(String username) {
        System.out.println("Tìm kiếm tài khoản với username: " + username);

        return accountRepository.findByUsername(username)
                .map(this::mapToUserDetails);
    }
}
