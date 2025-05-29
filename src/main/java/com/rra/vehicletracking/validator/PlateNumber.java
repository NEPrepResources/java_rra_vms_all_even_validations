package com.rra.vehicletracking.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PlateNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlateNumber {
    String message() default "Invalid plate number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}