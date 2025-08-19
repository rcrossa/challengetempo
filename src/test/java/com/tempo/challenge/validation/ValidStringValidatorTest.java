package com.tempo.challenge.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidStringValidatorTest {

    private ValidStringValidator validator;

    @Mock
    private ValidString validString;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ValidStringValidator();
        validator.initialize(validString);
    }

    @Test
    void initialize_shouldNotThrowException() {
        ValidStringValidator newValidator = new ValidStringValidator();
        assertDoesNotThrow(() -> newValidator.initialize(validString));
    }

    @ParameterizedTest
    @ValueSource(strings = {"John Doe 123", "John Doe", "Store123", "ABC Store 123"})
    void isValid_withValidStrings_shouldReturnTrue(String validValue) {
        boolean result = validator.isValid(validValue, context);
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_withNullValue_shouldReturnFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        boolean result = validator.isValid(null, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void isValid_withOnlyNumbers_shouldReturnFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String onlyNumbers = "12345";
        boolean result = validator.isValid(onlyNumbers, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void isValid_withSpecialCharacters_shouldReturnFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String withSpecialChars = "John@Doe";
        boolean result = validator.isValid(withSpecialChars, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void isValid_withSymbols_shouldReturnFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String withSymbols = "Store$123";
        boolean result = validator.isValid(withSymbols, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void isValid_withEmptyString_shouldReturnFalse() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String emptyString = "";
        boolean result = validator.isValid(emptyString, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("The field must contain only letters, numbers, and spaces");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void isValid_withSpacesOnly_shouldReturnTrue() {
        String spacesOnly = "   ";
        boolean result = validator.isValid(spacesOnly, context);

        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
    }
}
