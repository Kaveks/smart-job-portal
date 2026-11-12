package com.kaveks.userservice.controller;

import com.kaveks.commonweb.security.annotation.RequireRoles;
import com.kaveks.commonlib.domain.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequireRoles({UserRole.ROLE_ADMIN})
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "Job Portal User service ...... " + UserRole.ROLE_JOB_SEEKER;
    }
}
