package com.kaveks.companyservice.controller;

import com.kaveks.commonlib.domain.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/company")
    public String home(){
        return "Service for managing company profile, and documents " + UserRole.ROLE_EMPLOYER;
    }
}
