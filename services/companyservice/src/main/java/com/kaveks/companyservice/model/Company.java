package com.kaveks.companyservice.model;


import com.kaveks.commonlib.domain.*;
import com.kaveks.companyservice.utils.SocialLinks;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "company")
@NoArgsConstructor
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    private String tagLine;
    @Size(max=500)
    private String description;
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private Date dateFounded;
    private String phone;
    private String email;
    @Column(nullable = false)
    private Boolean verified =false;
    @Enumerated(EnumType.STRING)
    private CompanySize companySize;
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(unique = true,nullable = false)
    private String regNumber;
    private Long ownerId;
    @ElementCollection // create a different table
    private List<SocialLinks> socialLinks= new ArrayList<>();
    @Column(nullable = false)
    private Boolean active=true;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
