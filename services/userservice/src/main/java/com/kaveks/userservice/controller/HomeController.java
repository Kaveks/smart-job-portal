package com.kaveks.userservice.controller;


import com.kaveks.commonlib.domain.UserRole;
import com.kaveks.commonweb.security.annotation.AllowedRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllowedRoles({UserRole.ROLE_ADMIN})
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "Job Portal User service ...... " + UserRole.ROLE_JOB_SEEKER;
    }
}
