package com.rra.vehicletracking.controller;

import com.rra.vehicletracking.dto.PlateNumberDTO;
import com.rra.vehicletracking.entity.PlateNumber;
import com.rra.vehicletracking.service.PlateNumberService;
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

@Tag(name = "Plate Numbers", description = "Endpoints for managing vehicle plate numbers")
@RestController
@RequestMapping("/api/plate-numbers")
public class PlateNumberController {

    private final PlateNumberService plateNumberService;

    public PlateNumberController(PlateNumberService plateNumberService) {
        this.plateNumberService = plateNumberService;
    }

    @Operation(summary = "Register a plate number", description = "Admin registers a new plate number for an owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plate number registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, plate number exists, or owner not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PostMapping
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> registerPlateNumber(@Valid @RequestBody PlateNumberDTO plateNumberDTO) {
        plateNumberService.registerPlateNumber(plateNumberDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("plateNumber", plateNumberDTO.getPlateNumber());
        response.put("ownerId", plateNumberDTO.getOwnerId());
        response.put("issuedDate", plateNumberDTO.getIssuedDate());
        response.put("inUse", plateNumberDTO.isInUse());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate number registered successfully", response));
    }

    @Operation(summary = "List all plate numbers", description = "Retrieves a paginated list of all plate numbers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of plate numbers retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<PlateNumberDTO>>> listAllPlateNumbers(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<PlateNumberDTO> plateNumbers = plateNumberService.listAllPlateNumbers(page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate numbers retrieved successfully", plateNumbers));
    }

    @Operation(summary = "Get plate number by ID", description = "Retrieves a specific plate number by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plate number retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Plate number not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<PlateNumberDTO>> getPlateNumberById(
            @Parameter(description = "Plate number ID", example = "1") @PathVariable Long id) {
        PlateNumberDTO plateNumber = plateNumberService.getPlateNumberById(id);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate number retrieved successfully", plateNumber));
    }

    @Operation(summary = "List plate numbers by owner", description = "Retrieves a paginated list of plate numbers for a specific owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of plate numbers retrieved"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<PlateNumberDTO>>> listPlateNumbersByOwner(
            @Parameter(description = "Owner ID", example = "1") @PathVariable Long ownerId,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<PlateNumberDTO> plateNumbers = plateNumberService.listPlateNumbersByOwner(ownerId, page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate numbers retrieved successfully", plateNumbers));
    }

    @Operation(summary = "Update plate number", description = "Admin updates an existing plate number's details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plate number updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or plate number in use"),
            @ApiResponse(responseCode = "404", description = "Plate number or owner not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> updatePlateNumber(
            @Parameter(description = "Plate number ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PlateNumberDTO plateNumberDTO) {
        plateNumberService.updatePlateNumber(id, plateNumberDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("plateNumber", plateNumberDTO.getPlateNumber());
        response.put("ownerId", plateNumberDTO.getOwnerId());
        response.put("issuedDate", plateNumberDTO.getIssuedDate());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate number updated successfully", response));
    }

    @Operation(summary = "Delete plate number", description = "Admin deletes a plate number by ID, if not in use.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plate number deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plate number not found"),
            @ApiResponse(responseCode = "400", description = "Plate number is in use"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> deletePlateNumber(
            @Parameter(description = "Plate number ID", example = "1") @PathVariable Long id) {
        plateNumberService.deletePlateNumber(id);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "deleted");

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Plate number deleted successfully", response));
    }
}
