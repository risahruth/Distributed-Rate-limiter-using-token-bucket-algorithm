package com.risah.ratelimiter.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class TestController{
    @Value("${server.port}")
    private String port;

    @GetMapping("/test")
    public String test() {
        return "Allowed from port " + port;
    }
}