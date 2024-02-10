package org.sn.socialnetwork.security_and_config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sn.socialnetwork.service.CustomUserDetailsService;
import org.sn.socialnetwork.service.TwoFactorAuthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {

    final private CustomUserDetailsService userDetailsService;
    final private TwoFactorAuthService twoFactorAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if ("/2fa".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String otp = request.getParameter("otp");

            if (username != null && otp != null && twoFactorAuthService.verifyOtp(username, otp)) {
                // Assuming the OTP is valid, authenticate the user
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                response.sendRedirect("/home"); // Redirect to home or another secured page
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("2FA verification failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

