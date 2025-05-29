package com.rra.vehicletracking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlateNumberValidator implements ConstraintValidator<PlateNumber, String> {
    @Override
    public boolean isValid(String plateNumber, ConstraintValidatorContext context) {
        if (plateNumber == null) {
            return false;
        }
        // Format: RAA123B (RA, 3 digits, 1 letter)
        if (!plateNumber.matches("^RA[A-Z]\\d{3}[A-Z]$")) {
            return false;
        }
        // Exclude RDF, RNP, GR
        String prefix = plateNumber.substring(0, 3);
        return !prefix.equals("RDF") && !prefix.equals("RNP") && !prefix.equals("GR");
    }
}