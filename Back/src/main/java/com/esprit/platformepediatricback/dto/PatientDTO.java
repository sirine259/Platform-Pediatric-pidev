package com.esprit.platformepediatricback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String medicalRecordNumber;
    private Integer age;
    private String bloodType;
}
