package com.rra.vehicletracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Data transfer object for refresh token request")
@Data
public class RefreshTokenDTO {
    
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "JWT refresh token", required = true)
    private String refreshToken;
}