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
public class JobSkill {

    @Column(nullable = false, length = 100)
    private String name;
}
