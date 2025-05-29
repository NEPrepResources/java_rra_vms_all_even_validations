package com.rra.vehicletracking.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ChassisNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChassisNumber {
    String message() default "Invalid chassis number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}