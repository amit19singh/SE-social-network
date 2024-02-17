package org.sn.socialnetwork.security_and_config;

import lombok.AllArgsConstructor;
import org.sn.socialnetwork.service.CustomUserDetailsService;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/verify2fa","/verify", "/setup2fa",
                                "/password-reset-request", "/reset-password", "password-reset-security-check",
                        "/custom-login")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(twoFactorAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable();  // Use this only for API Testing, Use the following lines for web page
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true)
//                        .permitAll())
//                .logout(LogoutConfigurer::permitAll);

        http.oauth2Login(oauth2 -> oauth2
                        .loginPage("/custom-login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/custom-login?error=true")
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
                .allowedOrigins("http://localhost:3000") // The URL of your React app
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
