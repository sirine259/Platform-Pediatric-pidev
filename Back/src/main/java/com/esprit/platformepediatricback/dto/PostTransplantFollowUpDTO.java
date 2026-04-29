package com.esprit.platformepediatricback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTransplantFollowUpDTO {
    
    private Long id;
    private Long transplantId;
    private String followUpDate;
    private String immunosuppressiveTreatment;
    private String observations;
    private Double creatinineLevel;
    private Double gfr;
}
