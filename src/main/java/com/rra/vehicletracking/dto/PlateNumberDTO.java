package com.rra.vehicletracking.dto;

import com.rra.vehicletracking.validator.PlateNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlateNumberDTO {
    @Schema(description = "Unique identifier of the plate number", example = "1")
    private Long id;
    @PlateNumber(message = "Invalid plate number format")
    private String plateNumber;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Issued date is required")
    private LocalDate issuedDate;
}
