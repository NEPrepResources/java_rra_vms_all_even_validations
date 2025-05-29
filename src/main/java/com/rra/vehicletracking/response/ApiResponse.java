package com.rra.vehicletracking.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    @Schema(description = "Response status", example = "success", required = true)
    private String status;

    @Schema(description = "Response message", example = "Operation completed successfully", required = true)
    private String message;

    @Schema(description = "Response data payload", nullable = true)
    private T data;

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("success", message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message);
    }
}
