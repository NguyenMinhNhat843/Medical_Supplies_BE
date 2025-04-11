package com.user.userservice.security;

import com.user.userservice.constant.SystemContant;
import com.user.userservice.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }


    public String determineTargetUrl(Authentication authentication) {
        String url = "";
        String roles = String.valueOf(SecurityUtils.getAuthorities());
        if (isUser(roles)) {
            url = "/home";
        } else if (isAdmin(roles)) {
            url = "/admin/home";
        }
        return url;
    }


    public boolean isUser(String roles) {
        if (roles.contains("ROLE_USER")) {
            return true;
        }
        return false;
    }

    public boolean isAdmin(String roles) {
        if (roles.contains(SystemContant.ADMIN_ROLE) || roles.contains(SystemContant.STAFF_ROLE)) {
            return true;
        }
        return false;
    }

}
