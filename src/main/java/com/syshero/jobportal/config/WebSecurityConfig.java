package com.syshero.jobportal.config;

import com.syshero.jobportal.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.swing.*;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    private final String[] publicUrl = { "/",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css",
            "/*.js",
            "/*.js.map",
            "/fonts**", "/favicon.ico", "/resources/**", "/error" };

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Thêm một AuthenticationProvider tuỳ chỉnh vào cấu hình bảo mật HttpSecurity
        http.authenticationProvider(authenticationProvider());

        // Định cấu hình quyền truy cập cho các request HTTP
        http.authorizeHttpRequests(auth -> {
            // Cho phép truy cập không cần xác thực đối với các URL được xác định trong publicUrl
            auth.requestMatchers(publicUrl).permitAll();
            // Mọi request khác cần phải được xác thực
            auth.anyRequest().authenticated();
        });

        // Cấu hình đăng nhập sử dụng form
        http.formLogin(form -> form.loginPage("/login").permitAll()
                // Sử dụng AuthenticationSuccessHandler để xử lý khi đăng nhập thành công
                .successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        // Xây dựng và trả về đối tượng SecurityFilterChain
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Sử dụng BCryptPasswordEncoder để mã hoá mật khẩu
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        // Sử dụng một dịch vụ UserDetailsService tuỳ chỉnh để tải thông tin người dùng
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
