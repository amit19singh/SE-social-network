package org.sn.socialnetwork.security_and_config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.sn.socialnetwork.service.CustomUserDetailsService;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService userDetailsService;
    private final TwoFactorAuthService twoFactorAuthService;
    final private JwtTokenProvider jwtTokenProvider;
    final private CustomUserDetailsService customUserDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/verify2fa","/verify", "/setup2fa",
                                "/password-reset-request", "/reset-password", "password-reset-security-check",
                        "/custom-login", "/check-user")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(twoFactorAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable();  // Use this only for API Testing, Use the following lines for web page
//                .formLogin(form -> form
////                        .loginPage("/temp_landing")
//                        .defaultSuccessUrl("/home", true)
//                        .failureUrl("http://localhost:3000/login")
//                        .permitAll())
//                .logout(LogoutConfigurer::permitAll);

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"message\": \"Access Denied\"}");
                });


        http.oauth2Login(oauth2 -> oauth2
                        .loginPage("/custom-login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/temp_landing")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/custom-login")
                );


        http.cors(cors -> cors
                .configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*")); // For testing purposes only
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                })
        );

        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
//                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public TwoFactorAuthenticationFilter twoFactorAuthenticationFilter() {
        return new TwoFactorAuthenticationFilter(userDetailsService, twoFactorAuthService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public FieldEncryptor fieldEncryptor() {
        return new FieldEncryptor();
    }

}
