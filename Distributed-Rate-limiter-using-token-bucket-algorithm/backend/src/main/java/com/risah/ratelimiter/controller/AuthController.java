package com.risah.ratelimiter.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

import com.risah.ratelimiter.ratelimiterapp.service.RedisUserService;
import com.risah.ratelimiter.ratelimiterapp.security.JwtUtil;
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RedisUserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(RedisUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        if (!userService.validateUser(request.getUsername(), request.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(request.getUsername());

        return Collections.singletonMap("token", token);
    }

    // Optional: endpoint to add users (for dev/testing)
    @PostMapping("/register")
    public String register(@RequestBody LoginRequest request) {
        userService.saveUser(request.getUsername(), request.getPassword());
        return "User added";
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}