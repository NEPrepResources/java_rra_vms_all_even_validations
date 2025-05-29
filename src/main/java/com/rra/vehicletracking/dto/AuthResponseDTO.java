package com.rra.vehicletracking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication response containing tokens and user details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    @Schema(description = "JWT access token for API authorization")
    private String accessToken;
    
    @Schema(description = "JWT refresh token for obtaining new access tokens")
    private String refreshToken;
    
    @Schema(description = "Type of token (always 'Bearer')")
    private String tokenType = "Bearer";
    
    @Schema(description = "User ID")
    private Long userId;
    
    @Schema(description = "User's full name")
    private String name;
    
    @Schema(description = "User's email address")
    private String email;
    
    @Schema(description = "User's role")
    private String role;
    
    @Schema(description = "User's address")
    private String address;
    
    @Schema(description = "User's phone number")
    private String phone;
    
    @Schema(description = "User's national ID")
    private String nationalId;
}