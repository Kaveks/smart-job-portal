package com.kaveks.jobservice.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class JobTag {

    @Column(nullable = false, length = 50)
    private String name;
}