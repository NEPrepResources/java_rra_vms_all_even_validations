package com.rra.vehicletracking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VehicleTransferDTO {
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "From owner ID is required")
    private Long fromOwnerId;

    @NotNull(message = "To owner ID is required")
    private Long toOwnerId;

    @Positive(message = "Transfer price must be positive")
    private Double transferPrice;

    @NotNull(message = "New plate number ID is required")
    private Long newPlateNumberId;
}