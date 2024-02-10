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

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final TwoFactorAuthService twoFactorAuthService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/verify").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(twoFactorAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable();  // Use this only for API Testing, Use the following lines for web page
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true)
//                        .permitAll())
//                .logout(LogoutConfigurer::permitAll);
        return http.build();
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

}
