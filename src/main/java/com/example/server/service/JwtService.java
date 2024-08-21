package com.example.server.service;

import com.example.server.token.JwtIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JwtService {

    @Autowired
    private JwtIssueService jwtIssueService;

    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String validateTokenAndGetUserId(String token) {
        if (token == null) {
            return null;
        }

        Map<String, Object> claims = jwtIssueService.validateTokenAndGetClaims(token);
        if (claims == null) {
            return null;
        }

        return (String) claims.get("userId");
    }
}