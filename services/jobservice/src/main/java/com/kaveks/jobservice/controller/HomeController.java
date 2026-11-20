package com.kaveks.jobservice.controller;

import com.kaveks.commonlib.domain.user.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/job")
    public String home(){
        return "Service for managing jobs  " + UserRole.ROLE_JOB_SEEKER;
    }
}
