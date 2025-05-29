package com.rra.vehicletracking.dto;

import com.rra.vehicletracking.validator.RwandanNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for updating user information")
public class UserUpdateDTO {
    @NotBlank(message = "Name is required")
    @Schema(description = "Full name of the user", example = "John Doe", required = true)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address of the user", example = "john.doe@example.com", required = true)
    private String email;

    @Pattern(regexp = "^\\+2507\\d{8}$", message = "Phone number must be in format +2507xxxxxxxx")
    @Schema(description = "Phone number in Rwandan format", example = "+250712345678", required = true)
    private String phone;

    @RwandanNationalId(message = "Invalid Rwandan National ID")
    @Schema(description = "Rwandan National ID (16 digits)", example = "1199012345678901", required = true)
    private String nationalId;

    @NotNull(message = "Role is required")
    @Schema(description = "User role (ADMIN or USER)", example = "USER", allowableValues = {"ADMIN", "USER"}, required = true)
    private String role;
}
