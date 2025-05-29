package com.rra.vehicletracking.dto;

import com.rra.vehicletracking.validator.PlateNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlateNumberDTO {
    @PlateNumber(message = "Invalid plate number format")
    private String plateNumber;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Issued date is required")
    private LocalDate issuedDate;
}