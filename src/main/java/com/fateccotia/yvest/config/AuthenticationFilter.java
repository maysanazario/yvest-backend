package com.fateccotia.yvest.config;

import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.service.AuthService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

    private final ApplicationContext applicationContext;

    public AuthenticationFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String token = extractToken(httpRequest);
        
        // Endpoints públicos, não precisam autenticar 
        if (isPublicEndpoint(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Para endpoints protegidos, necessidade de token
        AuthService authService = applicationContext.getBean(AuthService.class);
        
        if (token != null && authService.validate(token)) {
            User user = authService.toUser(token);
            if (user != null) {
                httpRequest.setAttribute("user", user);
                chain.doFilter(request, response);
                return;
            }
        }
        
        // Token inválido ou ausente
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write("Token de autenticação inválido ou expirado");
    }
    
    //extrair token
    private String extractToken(HttpServletRequest request) {
        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }
        
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader)) {
            return authHeader;
        }
        
        return null;
    }
    
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Endpoints públicos
        return (path.equals("/api/auth/signup") && method.equals("POST")) ||
               (path.equals("/api/auth/signin") && method.equals("POST")) ||
               (path.equals("/api/auth/validate") && method.equals("POST"));
    }
}