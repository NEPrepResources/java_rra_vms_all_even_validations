package com.rra.vehicletracking.controller;

import com.rra.vehicletracking.dto.VehicleDTO;
import com.rra.vehicletracking.dto.VehicleTransferDTO;
import com.rra.vehicletracking.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vehicles", description = "Endpoints for managing vehicles and ownership transfers")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Register a vehicle", description = "Admin registers a new vehicle with owner and plate number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, chassis number exists, or plate number in use"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PostMapping
    public ResponseEntity<?> registerVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.registerVehicle(vehicleDTO);
        return ResponseEntity.ok("Vehicle registered successfully");
    }

    @Operation(summary = "List all vehicles", description = "Retrieves a paginated list of all registered vehicles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of vehicles retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> listVehicles(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vehicleService.listVehicles(page, size));
    }

    @Operation(summary = "Search vehicles", description = "Searches vehicles by owner's national ID, plate number, or chassis number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicles matching criteria retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<VehicleDTO>> searchVehicle(
            @Parameter(description = "Search query (national ID, plate number, or chassis number)", example = "RAA123A") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vehicleService.searchVehicle(query, page, size));
    }

    @Operation(summary = "Transfer vehicle ownership", description = "Admin transfers a vehicle to a new owner with a new plate number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle transferred successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, vehicle/owner not found, or plate number in use"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PostMapping("/transfer")
    public ResponseEntity<?> transferVehicle(@Valid @RequestBody VehicleTransferDTO transferDTO) {
        vehicleService.transferVehicle(transferDTO);
        return ResponseEntity.ok("Vehicle transferred successfully");
    }

    @Operation(summary = "Get vehicle ownership history by identifier", description = "Retrieves ownership history by chassis number or plate number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ownership history retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid identifier"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/history")
    public ResponseEntity<Page<VehicleTransferDTO>> getOwnershipHistory(
            @Parameter(description = "Chassis number or plate number", example = "RAA123A") @RequestParam String identifier,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vehicleService.getOwnershipHistory(identifier, page, size));
    }

    @Operation(summary = "Get vehicle ownership history by ID", description = "Retrieves ownership history for a specific vehicle by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ownership history retrieved"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/history/{id}")
    public ResponseEntity<Page<VehicleTransferDTO>> getOwnershipHistoryByVehicleId(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vehicleService.getOwnershipHistoryByVehicleId(id, page, size));
    }

    @Operation(summary = "Update vehicle details", description = "Admin updates an existing vehicle's details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or plate number in use"),
            @ApiResponse(responseCode = "404", description = "Vehicle, owner, or plate number not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.updateVehicle(id, vehicleDTO);
        return ResponseEntity.ok("Vehicle updated successfully");
    }

    @Operation(summary = "Delete vehicle", description = "Admin deletes a vehicle by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }
}