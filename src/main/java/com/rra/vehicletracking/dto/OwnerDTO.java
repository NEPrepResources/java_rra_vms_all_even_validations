package com.rra.vehicletracking.dto;

import com.rra.vehicletracking.validator.RwandanNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for vehicle owner information")
public class OwnerDTO {
    @NotBlank(message = "Name is required")
    @Schema(description = "Full name of the owner", example = "John Doe", required = true)
    private String name;

    @RwandanNationalId(message = "Invalid Rwandan National ID")
    @Schema(description = "Rwandan National ID (16 digits)", example = "1199012345678901", required = true)
    private String nationalId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the owner", example = "john.doe@example.com", required = true)
    private String email;

    @Pattern(regexp = "^\\+2507\\d{8}$", message = "Phone number must be in format +2507xxxxxxxx")
    @Schema(description = "Phone number in Rwandan format", example = "+250712345678", required = true)
    private String phone;

    @NotBlank(message = "Address is required")
    @Schema(description = "Physical address of the owner", example = "123 Main St, Kigali", required = true)
    private String address;
}
