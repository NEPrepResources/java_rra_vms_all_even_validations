package com.rra.vehicletracking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChassisNumberValidator implements ConstraintValidator<ChassisNumber, String> {
    @Override
    public boolean isValid(String chassisNumber, ConstraintValidatorContext context) {
        return chassisNumber != null && chassisNumber.matches("^[A-HJ-NPR-Z0-9]{17}$");
    }
}