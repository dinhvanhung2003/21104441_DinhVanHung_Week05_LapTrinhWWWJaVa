package vn.edu.iuh.fit.configs;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.iuh.fit.models.Account;
import vn.edu.iuh.fit.repositories.AccountRepository;
import vn.edu.iuh.fit.services.UserService;

@Configuration
public class SecurityConfig {
    @Autowired
    private AccountRepository accountRepository;
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/","/home","/register").permitAll() // Cho phép truy cập công khai
                        .requestMatchers("/jobs", "/jobs/**").permitAll() // Trang danh sách công việc công khai
                        .requestMatchers("/apply/**").authenticated() // Yêu cầu đăng nhập để ứng tuyển
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ admin truy cập
                        .requestMatchers("/recruiter/**").hasRole("RECRUITER") // Chỉ nhà tuyển dụng truy cập
                        .requestMatchers("/candidates/**").hasRole("CANDIDATE") // Chỉ ứng viên truy cập
                        .anyRequest().authenticated() // Yêu cầu xác thực cho các request khác
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Lấy thông tin đăng nhập từ Spring Security
                            String username = authentication.getName();

                            // Truy xuất thông tin tài khoản từ cơ sở dữ liệu
                            Account account = accountRepository.findByUsername(username)
                                    .orElseThrow(() -> new RuntimeException("Account not found"));

                            // Thiết lập accountId vào session
                            HttpSession session = request.getSession();
                            session.setAttribute("accountId", account.getId());
                            System.out.println("Saved accountId to session: " + account.getId());
                            // Kiểm tra lại session
                            Long accountIdFromSession = (Long) session.getAttribute("accountId");
                            System.out.println("Retrieved accountId from session: " + accountIdFromSession);
                            // Chuyển hướng dựa trên trạng thái tài khoản
                            if (account.getCandidate() == null) {
                                response.sendRedirect("/candidates/register/full-info");
                            } else {
                                response.sendRedirect("/home");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true") // Chuyển hướng sau khi đăng xuất
                        .permitAll()
                )
                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Xử lý khi truy cập bị từ chối
                            response.sendRedirect("/403");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Xử lý khi người dùng chưa xác thực
                            response.sendRedirect("/login");
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

