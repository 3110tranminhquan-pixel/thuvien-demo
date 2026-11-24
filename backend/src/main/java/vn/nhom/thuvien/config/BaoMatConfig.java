package vn.nhom.thuvien.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.nhom.thuvien.security.BoLocJwt;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class BaoMatConfig {

    private final BoLocJwt boLocJwt;

    public BaoMatConfig(BoLocJwt boLocJwt) {
        this.boLocJwt = boLocJwt;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())    // access token gửi trong header, không phải cookie
            .cors(cors -> cors.configurationSource(corsConfig()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sach/**", "/api/danh-muc/**").permitAll()
                .requestMatchers("/api/quan-tri/**").hasRole("QUAN_TRI")
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(boLocJwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // BoLocJwt là @Component nên Spring Boot sẽ tự đăng ký thêm một lần nữa vào chuỗi filter chung.
    // Tắt lần đăng ký đó đi, chỉ để nó chạy trong chuỗi của Spring Security.
    @Bean
    public FilterRegistrationBean<BoLocJwt> tatDangKyTuDong(BoLocJwt boLoc) {
        FilterRegistrationBean<BoLocJwt> dk = new FilterRegistrationBean<>(boLoc);
        dk.setEnabled(false);
        return dk;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);   // NFR-06
    }

    // Khi chạy dev, frontend đi qua proxy của Vite nên không cần CORS.
    // Cấu hình này để gọi thẳng từ trình duyệt ở cổng 5173 vẫn được.
    private CorsConfigurationSource corsConfig() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", c);
        return src;
    }
}
