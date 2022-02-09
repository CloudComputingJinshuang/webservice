package com.example.webserviceapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/healthz")
    public String testingAPI() {
        return "";
    }
}
