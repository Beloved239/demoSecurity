package com.demoProject.config;

import com.demoProject.exception.ResourceNotFoundException;
import com.demoProject.model.TokenEntity;
import com.demoProject.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        String jwt = authHeader.substring(7);
        TokenEntity token = tokenRepository.findByToken(jwt).orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        if (token != null){
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
        }

    }
}
