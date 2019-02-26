package com.fxmind.manager.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergei Zhuravlev
 */
public class MssAccessDeniedHandler implements AccessDeniedHandler {
    private String errorPageUrl;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect(request.getContextPath() + errorPageUrl);
    }

    public String getErrorPageUrl() {
        return errorPageUrl;
    }

    public void setErrorPageUrl(String errorPageUrl) {
        this.errorPageUrl = errorPageUrl;
    }
}
