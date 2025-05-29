package com.rra.vehicletracking.controller;

import com.rra.vehicletracking.dto.OwnerDTO;
import com.rra.vehicletracking.entity.Owner;
import com.rra.vehicletracking.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Owners", description = "Endpoints for managing vehicle owners")
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Operation(summary = "Register a new owner", description = "Admin registers a new vehicle owner with validated details. The owner's information must include a valid Rwandan National ID, name, email, phone number, and address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owner registered successfully", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = String.class),
                examples = {@io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "Owner registered successfully"
                )})),
            @ApiResponse(responseCode = "400", description = "Invalid input or national ID already exists", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class),
                examples = {@io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"error\": \"Owner with National ID 1199012345678901 already exists\"}"
                )})),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class),
                examples = {@io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"error\": \"Access denied\"}"
                )}))
    })
    @PostMapping
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> registerOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
        com.rra.vehicletracking.entity.Owner owner = ownerService.registerOwner(ownerDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("ownerId", owner.getId());
        response.put("name", owner.getName());
        response.put("nationalId", owner.getNationalId());
        response.put("email", owner.getEmail());
        response.put("phone", owner.getPhone());
        response.put("address", owner.getAddress());
        response.put("createdAt", owner.getCreatedAt());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Owner registered successfully", response));
    }

    @Operation(summary = "List all owners", description = "Retrieves a paginated list of all registered owners.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of owners retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping
    public ResponseEntity<Page<OwnerDTO>> listOwners(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ownerService.listOwners(page, size));
    }

    @Operation(summary = "Search owners", description = "Searches owners by national ID, email, or phone with pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of matching owners retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<OwnerDTO>> searchOwner(
            @Parameter(description = "Search query (national ID, email, or phone)", example = "1199005123456789") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ownerService.searchOwner(query, page, size));
    }

    @Operation(summary = "Update owner details", description = "Admin updates an existing owner's details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owner updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or national ID conflict"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> updateOwner(
            @Parameter(description = "Owner ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody OwnerDTO ownerDTO) {
        Owner owner = ownerService.updateOwner(id, ownerDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("ownerId", owner.getId());
        response.put("name", owner.getName());
        response.put("nationalId", owner.getNationalId());
        response.put("email", owner.getEmail());
        response.put("phone", owner.getPhone());
        response.put("address", owner.getAddress());
        response.put("updatedAt", owner.getUpdatedAt());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Owner updated successfully", response));
    }

    @Operation(summary = "Delete owner", description = "Admin deletes an owner by ID, if no vehicles are associated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owner deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "400", description = "Owner has associated vehicles"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> deleteOwner(
            @Parameter(description = "Owner ID", example = "1") @PathVariable Long id) {
        Owner owner = ownerService.deleteOwner(id);

        Map<String, Object> response = new HashMap<>();
        response.put("ownerId", owner.getId());
        response.put("name", owner.getName());
        response.put("nationalId", owner.getNationalId());
        response.put("email", owner.getEmail());
        response.put("phone", owner.getPhone());
        response.put("address", owner.getAddress());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Owner deleted successfully", response));
    }
}
