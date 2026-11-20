package com.kaveks.companyservice.controller;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.commonlib.domain.user.UserRole;
import com.kaveks.commonlib.dto.company.CompanyRequest;
import com.kaveks.commonlib.dto.company.CompanyResponse;
import com.kaveks.commonlib.dto.company.CompanyUpdateRequest;
import com.kaveks.commonweb.security.annotation.AllowedRoles;
import com.kaveks.companyservice.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    //create company
    @PostMapping
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<CompanyResponse>createCompany(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long ownerId= getOwnerId(jwt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(ownerId,request));
    }

    //company list(public)
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> companyList(
            @RequestParam(required = false)CompanyType type,
            @RequestParam(required = false)IndustryType industry,
            @RequestParam(required = false)CompanyStatus status
    ){
        return ResponseEntity.ok(
                companyService.companyList(type,industry,status)
        );
    }

    // get authenticated employer's companies
    @GetMapping("/my")
    public ResponseEntity<List<CompanyResponse>> getMyCompanies(
            @AuthenticationPrincipal Jwt jwt){
        Long ownerId = getOwnerId(jwt);
        return ResponseEntity.ok(companyService.getMyCompanies(ownerId));

    }
    // Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                companyService.getCompanyById(id)
        );
    }

    // Update company
    @PatchMapping("/{id}")
    @AllowedRoles({UserRole.ROLE_EMPLOYER})
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long ownerId = getOwnerId(jwt);

        return ResponseEntity.ok(
                companyService.updateCompany(id, ownerId, request)
        );
    }

    @GetMapping("/{id}/ownership")
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<Void> verifyCompanyOwnership(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long ownerId = getOwnerId(jwt);

        companyService.verifyCompanyOwnership(id, ownerId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<Void> verifyCompanyEligibility(
            @PathVariable Long id
    ) {
        companyService.verifyCompanyEligibility(id);
        return ResponseEntity.noContent().build();
    }

    // Verify company
    @PatchMapping("/{id}/verify")
    @AllowedRoles({UserRole.ROLE_ADMIN})
    public ResponseEntity<String> verifyCompany(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                companyService.verifyCompany(id)
        );
    }

    // Deactivate company
    @PatchMapping("/{id}/deactivate")
    @AllowedRoles({UserRole.ROLE_ADMIN})
    public ResponseEntity<String> deactivateCompany(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                companyService.deactivateCompany(id)
        );
    }

    // Suspend company
    @PatchMapping("/{id}/suspend")
    @AllowedRoles({UserRole.ROLE_ADMIN})
    public ResponseEntity<String> suspendCompany(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                companyService.suspendCompany(id)
        );
    }

    // Soft-delete company
    @DeleteMapping("/{id}")
    @AllowedRoles({UserRole.ROLE_EMPLOYER,UserRole.ROLE_ADMIN})
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long ownerId = getOwnerId(jwt);

        companyService.deleteCompany(id,ownerId);

        return ResponseEntity.noContent().build();
    }

    // Extract authenticated user ID from JWT subject
    private Long getOwnerId(Jwt jwt) {
        String subject = jwt.getSubject();
        assert subject != null;
        return Long.valueOf(subject);
    }

}
