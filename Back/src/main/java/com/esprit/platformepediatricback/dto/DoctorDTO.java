package com.esprit.platformepediatricback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
}
