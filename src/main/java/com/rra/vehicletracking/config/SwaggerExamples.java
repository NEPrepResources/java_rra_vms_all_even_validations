package com.rra.vehicletracking.config;

import com.rra.vehicletracking.dto.OwnerDTO;
import com.rra.vehicletracking.dto.PlateNumberDTO;
import com.rra.vehicletracking.dto.VehicleDTO;
import com.rra.vehicletracking.dto.VehicleTransferDTO;
import com.rra.vehicletracking.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class contains example objects for Swagger documentation.
 * These examples are used to provide detailed request and response examples in the API documentation.
 */
@Schema(hidden = true)
public class SwaggerExamples {

    /**
     * Example of a successful API response with a string message
     */
    public static final ApiResponse<String> SUCCESS_RESPONSE = 
        new ApiResponse<>("success", "Operation completed successfully", null);

    /**
     * Example of an error API response
     */
    public static final ApiResponse<Object> ERROR_RESPONSE = 
        new ApiResponse<>("error", "An error occurred", null);

    /**
     * Example of a validation error API response
     */
    public static final ApiResponse<Object> VALIDATION_ERROR_RESPONSE = 
        new ApiResponse<>("error", "Validation failed", null);

    /**
     * Example of an owner DTO for request/response documentation
     */
    public static final OwnerDTO OWNER_EXAMPLE = createOwnerExample();

    /**
     * Example of a vehicle DTO for request/response documentation
     */
    public static final VehicleDTO VEHICLE_EXAMPLE = createVehicleExample();

    /**
     * Example of a plate number DTO for request/response documentation
     */
    public static final PlateNumberDTO PLATE_NUMBER_EXAMPLE = createPlateNumberExample();

    /**
     * Example of a vehicle transfer DTO for request/response documentation
     */
    public static final VehicleTransferDTO VEHICLE_TRANSFER_EXAMPLE = createVehicleTransferExample();

    private static OwnerDTO createOwnerExample() {
        OwnerDTO owner = new OwnerDTO();
        owner.setName("John Doe");
        owner.setNationalId("1199012345678901");
        owner.setEmail("john.doe@example.com");
        owner.setPhone("+250712345678");
        owner.setAddress("123 Main St, Kigali");
        return owner;
    }

    private static VehicleDTO createVehicleExample() {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setManufactureCompany("Toyota");
        vehicle.setModelName("Corolla");
        vehicle.setManufactureYear(2020);
        vehicle.setPrice(15000.0);
        vehicle.setChassisNumber("JTDKN3DU0A0123456");
        vehicle.setOwnerId(1L);
        vehicle.setPlateNumberId(1L);
        return vehicle;
    }

    private static PlateNumberDTO createPlateNumberExample() {
        PlateNumberDTO plateNumber = new PlateNumberDTO();
        plateNumber.setPlateNumber("RAA123A");
        plateNumber.setOwnerId(1L);
        plateNumber.setIssuedDate(java.time.LocalDate.now());
        return plateNumber;
    }

    private static VehicleTransferDTO createVehicleTransferExample() {
        VehicleTransferDTO transfer = new VehicleTransferDTO();
        transfer.setVehicleId(1L);
        transfer.setFromOwnerId(1L);
        transfer.setToOwnerId(2L);
        transfer.setTransferPrice(10000.0);
        transfer.setNewPlateNumberId(2L);
        return transfer;
    }
}
