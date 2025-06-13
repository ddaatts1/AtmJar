package com.mitec.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long id;
    private String template;
    private String name;
    private Long contractId;

    // getters and setters
}