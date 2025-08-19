package com.tempo.challenge.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStringValidator implements ConstraintValidator<ValidString, String> {

    @Override
    public void initialize(ValidString constraintAnnotation) {
        // No initialization required - this validator doesn't need any configuration from the annotation
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches("^[a-zA-Z0-9\\s]+$") || value.matches("^[0-9]+$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}