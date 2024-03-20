package org.sn.socialnetwork.security_and_config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    // Constructor
    public CustomOAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Generate JWT

        System.out.println("HERE");
        String jwt = jwtTokenProvider.generateToken(authentication);

        System.out.println(jwt);

        // Option 1: Redirect user with JWT in query parameter
        response.sendRedirect("/your-success-url?token=" + jwt);

        // Option 2: Set JWT in HTTP Cookie (choose one approach)
        // CookieUtils.createCookie(response, "JWT_COOKIE", jwt, COOKIE_EXPIRY);
    }
}
