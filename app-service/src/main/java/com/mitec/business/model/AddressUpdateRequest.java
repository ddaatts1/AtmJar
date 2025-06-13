package com.mitec.business.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateRequest {

    private String serialNumber;
    private String newAddress;

    // Getters and setters
}