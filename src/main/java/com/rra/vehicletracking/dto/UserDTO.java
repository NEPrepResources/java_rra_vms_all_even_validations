package com.rra.vehicletracking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rra.vehicletracking.entity.Role;
import com.rra.vehicletracking.validator.RwandanNationalId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for user registration")
public class UserDTO {
    @NotBlank(message = "Name is required")
    @Schema(description = "Full name of the user", example = "John Doe", required = true)
    private String name;

    @NotBlank(message = "Address is required")
    @Schema(description = "Physical address of the user", example = "123 Main St, Kigali", required = true)
    private String address;

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

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters with uppercase, lowercase, digit, and special character")
    @Schema(description = "User password (min 8 chars with uppercase, lowercase, digit, and special character)", 
           example = "Password1!", required = true, minLength = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Role is required")
    @Schema(description = "User role (ADMIN or USER)", example = "USER", allowableValues = {"ADMIN", "USER"}, required = true)
    private String role;
}
