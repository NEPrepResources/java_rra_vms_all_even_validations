package com.rra.vehicletracking.dto;

import com.rra.vehicletracking.validator.ChassisNumber;
import com.rra.vehicletracking.validator.PlateNumber;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehicleDTO {
    @ChassisNumber(message = "Invalid chassis number")
    private String chassisNumber;

    @NotBlank(message = "Manufacture company is required")
    private String manufactureCompany;

    @Min(value = 1900, message = "Manufacture year must be after 1900")
    @Max(value = 2025, message = "Manufacture year cannot be in the future")
    private Integer manufactureYear;

    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Plate number ID is required")
    private Long plateNumberId;
}