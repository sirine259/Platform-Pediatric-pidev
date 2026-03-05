package com.esprit.platformepediatricback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransplantDTO {
    
    private Long id;
    private String transplantDate;
    private String status;
    private String notes;
    private Long medicalRecordId;
    private Long doctorId;
}
