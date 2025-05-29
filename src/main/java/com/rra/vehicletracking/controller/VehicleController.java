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

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> registerVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.registerVehicle(vehicleDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("chassisNumber", vehicleDTO.getChassisNumber());
        response.put("manufactureCompany", vehicleDTO.getManufactureCompany());
        response.put("manufactureYear", vehicleDTO.getManufactureYear());
        response.put("price", vehicleDTO.getPrice());
        response.put("modelName", vehicleDTO.getModelName());
        response.put("ownerId", vehicleDTO.getOwnerId());
        response.put("plateNumberId", vehicleDTO.getPlateNumberId());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle registered successfully", response));
    }

    @Operation(summary = "List all vehicles", description = "Retrieves a paginated list of all registered vehicles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of vehicles retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<VehicleDTO>>> listVehicles(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<VehicleDTO> vehicles = vehicleService.listVehicles(page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicles retrieved successfully", vehicles));
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<VehicleDTO>> getVehicleById(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id) {
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle retrieved successfully", vehicle));
    }

    @Operation(summary = "Search vehicles", description = "Searches vehicles by owner's national ID, plate number, or chassis number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicles matching criteria retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/search")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<VehicleDTO>>> searchVehicle(
            @Parameter(description = "Search query (national ID, plate number, or chassis number)", example = "RAA123A") @RequestParam String query,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<VehicleDTO> vehicles = vehicleService.searchVehicle(query, page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Matching vehicles retrieved successfully", vehicles));
    }

    @Operation(
        summary = "Transfer vehicle ownership", 
        description = "Admin transfers a vehicle to a new owner with a new plate number. " +
                "The request body must be a valid JSON object with vehicleId, fromOwnerId, toOwnerId, transferPrice, and newPlateNumberId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle transferred successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, vehicle/owner not found, or plate number in use"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Vehicle transfer details",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rra.vehicletracking.dto.VehicleTransferDTO.class),
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Vehicle Transfer Example",
                value = "{\n" +
                        "  \"vehicleId\": 1,\n" +
                        "  \"fromOwnerId\": 1,\n" +
                        "  \"toOwnerId\": 2,\n" +
                        "  \"transferPrice\": 10000.0,\n" +
                        "  \"newPlateNumberId\": 2\n" +
                        "}"
            )
        )
    )
    @PostMapping("/transfer")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> transferVehicle(@Valid @RequestBody VehicleTransferDTO transferDTO) {
        vehicleService.transferVehicle(transferDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("vehicleId", transferDTO.getVehicleId());
        response.put("fromOwnerId", transferDTO.getFromOwnerId());
        response.put("toOwnerId", transferDTO.getToOwnerId());
        response.put("transferPrice", transferDTO.getTransferPrice());
        response.put("newPlateNumberId", transferDTO.getNewPlateNumberId());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle transferred successfully", response));
    }

    @Operation(summary = "Get vehicle ownership history by identifier", description = "Retrieves ownership history by chassis number or plate number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ownership history retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid identifier"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/history")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<VehicleTransferDTO>>> getOwnershipHistory(
            @Parameter(description = "Chassis number or plate number", example = "RAA123A") @RequestParam String identifier,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<VehicleTransferDTO> history = vehicleService.getOwnershipHistory(identifier, page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle ownership history retrieved successfully", history));
    }

    @Operation(summary = "Get vehicle ownership history by ID", description = "Retrieves ownership history for a specific vehicle by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ownership history retrieved"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @GetMapping("/history/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Page<VehicleTransferDTO>>> getOwnershipHistoryByVehicleId(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<VehicleTransferDTO> history = vehicleService.getOwnershipHistoryByVehicleId(id, page, size);
        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle ownership history retrieved successfully", history));
    }

    @Operation(summary = "Update vehicle details", description = "Admin updates an existing vehicle's details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or plate number in use"),
            @ApiResponse(responseCode = "404", description = "Vehicle, owner, or plate number not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> updateVehicle(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.updateVehicle(id, vehicleDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("chassisNumber", vehicleDTO.getChassisNumber());
        response.put("manufactureCompany", vehicleDTO.getManufactureCompany());
        response.put("manufactureYear", vehicleDTO.getManufactureYear());
        response.put("price", vehicleDTO.getPrice());
        response.put("modelName", vehicleDTO.getModelName());
        response.put("ownerId", vehicleDTO.getOwnerId());
        response.put("plateNumberId", vehicleDTO.getPlateNumberId());

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle updated successfully", response));
    }

    @Operation(summary = "Delete vehicle", description = "Admin deletes a vehicle by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found"),
            @ApiResponse(responseCode = "403", description = "Access denied (admin role required)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.rra.vehicletracking.response.ApiResponse<Map<String, Object>>> deleteVehicle(
            @Parameter(description = "Vehicle ID", example = "1") @PathVariable Long id) {
        vehicleService.deleteVehicle(id);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("status", "deleted");

        return ResponseEntity.ok(com.rra.vehicletracking.response.ApiResponse.success("Vehicle deleted successfully", response));
    }
}
