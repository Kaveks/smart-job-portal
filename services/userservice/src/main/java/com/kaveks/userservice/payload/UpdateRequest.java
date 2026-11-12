package com.kaveks.userservice.payload;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRequest {

    @Size(max = 100)
    private String firstName;


    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    private String profileImage;
}