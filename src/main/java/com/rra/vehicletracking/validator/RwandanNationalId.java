package com.rra.vehicletracking.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = RwandanNationalIdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RwandanNationalId {
    String message() default "Invalid Rwandan National ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
