package com.rra.vehicletracking.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class RwandanNationalIdValidator implements ConstraintValidator<RwandanNationalId, String> {

    @Override
    public boolean isValid(String nationalId, ConstraintValidatorContext context) {
        if (nationalId == null || nationalId.length() != 16 || !nationalId.matches("\\d{16}")) {
            return buildConstraintViolation(context, "National ID must be exactly 16 numeric digits.");
        }

        // G: Group (1-Rwandan, 2-Refugee, 3-Foreigner)
        char group = nationalId.charAt(0);
        if (group != '1' && group != '2' && group != '3') {
            return buildConstraintViolation(context, "First digit must be 1 (Rwandan), 2 (Refugee), or 3 (Foreigner).");
        }

        // YYYY: Birth year (digits 2-5)
        int year;
        try {
            year = Integer.parseInt(nationalId.substring(1, 5));
            int currentYear = Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                return buildConstraintViolation(context, "Birth year must be between 1900 and " + currentYear + ".");
            }
        } catch (NumberFormatException e) {
            return buildConstraintViolation(context, "Birth year in National ID is invalid.");
        }

        // Gender digit (digit 6)
        char gender = nationalId.charAt(5);
        if (gender != '7' && gender != '8') {
            return buildConstraintViolation(context, "Gender digit must be 7 (female) or 8 (male).");
        }

        // Unique number (digits 7-13, index 6 to 12)
        String uniqueNumber = nationalId.substring(6, 13);
        if (uniqueNumber.equals("0000000")) {
            return buildConstraintViolation(context, "Unique number part of National ID cannot be all zeros.");
        }

        // I: index digit (digit 14, index 13)
        char indexDigit = nationalId.charAt(13);
        if (!Character.isDigit(indexDigit)) {
            return buildConstraintViolation(context, "Index digit must be numeric.");
        }

        // CC: last two digits (digits 15-16, index 14-15)
        String cc = nationalId.substring(14, 16);
        if (!cc.matches("\\d{2}")) {
            return buildConstraintViolation(context, "Last two digits of National ID must be numeric.");
        }

        return true;
    }

    private boolean buildConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
