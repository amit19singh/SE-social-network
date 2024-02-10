package org.sn.socialnetwork.security_and_config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Check if the request is for the 2FA verification endpoint
        if ("/2fa".equals(path)) {
            // Handle 2FA verification
            // This part is highly dependent on how you've implemented the 2FA verification logic
            // For example, you might check a request parameter or header for the 2FA code
            String otp = request.getParameter("otp");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && otp != null) {
                // Verify the OTP here. If verification is successful, proceed with the authentication
                // This might involve setting a new authentication object in the SecurityContext
            }
        } else {
            // For all other requests, just proceed without doing anything special
            filterChain.doFilter(request, response);
        }

    }
}


