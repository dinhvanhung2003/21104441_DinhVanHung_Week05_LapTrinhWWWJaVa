package vn.edu.iuh.fit.backend.configs;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.iuh.fit.backend.models.Account;
import vn.edu.iuh.fit.backend.repositories.AccountRepository;
import vn.edu.iuh.fit.backend.services.UserService;

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
                        .requestMatchers("/login", "/", "/home", "/register").permitAll() // Công khai
                        .requestMatchers("/jobs", "/jobs/**").permitAll() // Công khai danh sách công việc
                        .requestMatchers("/apply/**").authenticated() // Yêu cầu đăng nhập để ứng tuyển
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ Admin
                        .requestMatchers("/recruiter/**").hasRole("RECRUITER") // Chỉ Nhà tuyển dụng
                        .requestMatchers(HttpMethod.GET, "/recruiter/dashboard/info-company").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.POST, "/recruiter/dashboard/info-company").hasRole("RECRUITER")
                        .requestMatchers("/candidates/**").hasRole("CANDIDATE") // Chỉ Ứng viên
                        .requestMatchers("/dashboard/jobs/new").permitAll()
                        .anyRequest().authenticated() // Mọi request khác yêu cầu xác thực
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
                            if (account.getRole() != null) {
                                String roleName = account.getRole().getRoleName();

                                switch (roleName) {
                                    case "ROLE_CANDIDATE":
                                        if (account.getCandidate() == null) {
                                            response.sendRedirect("/candidates/register/full-info");
                                        } else {
                                            response.sendRedirect("/candidates/dashboard");
                                        }
                                        break;

                                    case "ROLE_RECRUITER":
                                        response.sendRedirect("/recruiter/dashboard");
                                        break;

                                    case "ROLE_ADMIN":
                                        response.sendRedirect("/admin/dashboard");
                                        break;

                                    default:
                                        response.sendRedirect("/home");
                                        break;
                                }
                            }



                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout") // URL xử lý logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Xóa session khi logout
                            HttpSession session = request.getSession(false); // Lấy session hiện tại
                            if (session != null) {
                                session.invalidate(); // Xóa session
                            }

                            // Chuyển hướng đến trang login với thông báo
                            response.sendRedirect("/login?logout=true");
                        })
                        .invalidateHttpSession(true) // Vô hiệu hóa session
                        .deleteCookies("JSESSIONID") // Xóa cookie của phiên làm việc
                        .permitAll() // Cho phép mọi người truy cập
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

