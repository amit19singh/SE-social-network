package org.sn.socialnetwork.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OAuth2LoginController {

    @GetMapping("/custom-login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
    @GetMapping("/custom-login-success")
    public ModelAndView customLoginSuccess(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            // Custom logic after successful OAuth2 login
            // For example, redirecting based on the role or doing some logging
            // Here, we're just redirecting to the home page as an example
            return new ModelAndView("redirect:/home");
        }

        // Fallback redirection if the authentication type is not OAuth2
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/custom-logout")
    public String customLogout(HttpServletRequest request, HttpServletResponse response) {
        // Custom logic before logout
        // For example, logging the logout event

        // Invalidate the session and clear the security context
        SecurityContextHolder.clearContext();
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }

        // Redirect to a custom page after logout
        return "redirect:/login?logout";
    }
}
