package com.rra.vehicletracking.controller;

import com.rra.vehicletracking.dto.AuthResponseDTO;
import com.rra.vehicletracking.dto.LoginDTO;
import com.rra.vehicletracking.dto.RefreshTokenDTO;
import com.rra.vehicletracking.dto.UserDTO;
import com.rra.vehicletracking.entity.User;
import com.rra.vehicletracking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "User authentication and management endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user",
            description = "Creates a new user account with the provided details. Only admins can assign roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully with detailed information"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.signup(userDTO);

        // Create a response with detailed user information
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("address", user.getAddress());
        response.put("phone", user.getPhone());
        response.put("nationalId", user.getNationalId());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User login",
            description = "Authenticates a user with email and password, returns JWT tokens and user details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT tokens and user details returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponse = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh access token",
            description = "Uses a refresh token to generate a new access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access token generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        AuthResponseDTO authResponse = userService.refreshToken(refreshTokenDTO.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Initialize admin user",
            description = "Creates a default admin user if it doesn't exist. Accessible without authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin user created successfully"),
            @ApiResponse(responseCode = "400", description = "Admin user already exists")
    })
    @GetMapping("/init-admin")
    public ResponseEntity<?> initAdmin() {
        try {
            userService.initAdmin();
            return ResponseEntity.ok("Admin user created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "List all users",
            description = "Retrieves a paginated list of all users. Requires admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> listUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.listUsers(page, size));
    }
}
