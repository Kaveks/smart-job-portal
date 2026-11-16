package com.kaveks.companyservice.model;

import com.kaveks.commonlib.domain.company.*;
import com.kaveks.companyservice.utils.SocialLinks;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "company",
        indexes = {
                @Index(name = "idx_company_owner_id", columnList = "owner_id"),
                @Index(name = "idx_company_status", columnList = "status")
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, length = 180)
    private String slug;

    @Column(length = 200)
    private String tagLine;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    private String website;

    private String logoUrl;

    private String coverImageUrl;

    private LocalDate dateFounded;

    private String phone;

    private String email;

    @Column(nullable = false)
    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status = CompanyStatus.PENDING;

    @Column(unique = true, nullable = false)
    private String regNumber;

    @Column(nullable = false)
    private Long ownerId;

    @ElementCollection
    @CollectionTable(
            name = "company_social_links",
            joinColumns = @JoinColumn(name = "company_id")
    )
    private List<SocialLinks> socialLinks = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}