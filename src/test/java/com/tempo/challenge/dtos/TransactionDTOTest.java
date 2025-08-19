package com.tempo.challenge.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {

    private Validator validator;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        transactionDTO = new TransactionDTO();
    }


    // ===== TESTS DE CONSTRUCTOR =====

    @Test
    void defaultConstructor_shouldCreateEmptyObject() {
        TransactionDTO dto = new TransactionDTO();
        assertNull(dto.getId());
        assertNull(dto.getAmount());
        assertNull(dto.getMerchant());
        assertNull(dto.getClientName());
        assertNull(dto.getTransactionDate());
    }

    @Test
    void constructor_shouldCreateObjectWithAllFields() {
        LocalDate testDate = LocalDate.of(2023, 1, 15);

        TransactionDTO dto = new TransactionDTO();
        dto.setId(1L);
        dto.setAmount(100);
        dto.setMerchant("Test Merchant");
        dto.setClientName("Test Client");
        dto.setTransactionDate(testDate);

        assertEquals(1L, dto.getId());
        assertEquals(100, dto.getAmount());
        assertEquals("Test Merchant", dto.getMerchant());
        assertEquals("Test Client", dto.getClientName());
        assertEquals(testDate, dto.getTransactionDate());
    }

    @Test
    void setAndGetId_shouldWorkCorrectly() {
        Long testId = 123L;
        transactionDTO.setId(testId);
        assertEquals(testId, transactionDTO.getId());
    }

    @Test
    void setAndGetAmount_shouldWorkCorrectly() {
        Integer testAmount = 500;
        transactionDTO.setAmount(testAmount);
        assertEquals(testAmount, transactionDTO.getAmount());
    }

    @Test
    void setAndGetMerchant_shouldWorkCorrectly() {
        String testMerchant = "Amazon Store";
        transactionDTO.setMerchant(testMerchant);
        assertEquals(testMerchant, transactionDTO.getMerchant());
    }

    @Test
    void setAndGetClientName_shouldWorkCorrectly() {
        String testClient = "John Doe";
        transactionDTO.setClientName(testClient);
        assertEquals(testClient, transactionDTO.getClientName());
    }

    @Test
    void setAndGetTransactionDate_shouldWorkCorrectly() {
        LocalDate testDate = LocalDate.now();
        transactionDTO.setTransactionDate(testDate);
        assertEquals(testDate, transactionDTO.getTransactionDate());
    }

    // ===== TESTS DE VALIDACIÓN EXITOSA =====

    @ParameterizedTest
    @MethodSource("validValidationTestCases")
    void validTransactionDTO_ParameterizedScenarios_shouldPassValidation(String testName, Integer amount, String merchant, String clientName, LocalDate transactionDate) {
        transactionDTO.setAmount(amount);
        transactionDTO.setMerchant(merchant);
        transactionDTO.setClientName(clientName);
        transactionDTO.setTransactionDate(transactionDate);

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertTrue(violations.isEmpty(), "Test case '" + testName + "' should pass validation");
    }

    static Stream<Arguments> validValidationTestCases() {
        LocalDate now = LocalDate.now();
        LocalDate past = now.minusDays(1);

        return Stream.of(
                Arguments.of("validTransactionDTO", 100, "Valid Store 123", "John Doe 456", now),
                Arguments.of("pastTransactionDate", 100, "Valid Store", "John Doe", past),
                Arguments.of("presentTransactionDate", 100, "Valid Store", "John Doe", now),
                Arguments.of("minimumValidAmount", 1, "Valid Store", "John Doe", now),
                Arguments.of("largeAmount", 999999, "Valid Store", "John Doe", now),
                Arguments.of("merchantWithNumbers", 100, "Store 123", "John Doe", now),
                Arguments.of("clientNameWithNumbers", 100, "Valid Store", "John Doe 123", now),
                Arguments.of("merchantWithSpaces", 100, "My Favorite Store", "John Doe", now),
                Arguments.of("clientNameWithSpaces", 100, "Valid Store", "John Middle Doe", now)
        );
    }

    // ===== TESTS DE VALIDACIÓN CON ERRORES =====

    @Test
    void nullAmount_shouldFailValidation() {
        transactionDTO.setAmount(null);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("amount is required")));
    }

    @Test
    void zeroAmount_shouldFailValidation() {
        transactionDTO.setAmount(0);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be greater than 0")));
    }

    @Test
    void negativeAmount_shouldFailValidation() {
        transactionDTO.setAmount(-10);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be greater than 0")));
    }

    @Test
    void blankMerchant_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("merchant is required")));
    }

    @Test
    void nullMerchant_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant(null);
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("merchant is required")));
    }

    @Test
    void merchantWithSpecialCharacters_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Store@Invalid#");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must contain only letters, numbers, and spaces")));
    }

    @Test
    void blankClientName_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("name of the client is required")));
    }

    @Test
    void nullClientName_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName(null);
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("name of the client is required")));
    }

    @Test
    void clientNameWithSpecialCharacters_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John@Doe#");
        transactionDTO.setTransactionDate(LocalDate.now());

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must contain only letters, numbers, and spaces")));
    }

    @Test
    void nullTransactionDate_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(null);

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("date of the transaction is required")));
    }

    @Test
    void futureTransactionDate_shouldFailValidation() {
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Valid Store");
        transactionDTO.setClientName("John Doe");
        transactionDTO.setTransactionDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be in the past or present")));
    }

    // ===== TESTS ADICIONALES =====

    @Test
    void allNullFields_shouldHaveMultipleValidationErrors() {
        TransactionDTO dto = new TransactionDTO();

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 4); // amount, merchant, clientName, transactionDate
    }

    @Test
    void setFieldsToNull_shouldWork() {
        // Set fields first
        transactionDTO.setId(1L);
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Store");
        transactionDTO.setClientName("Client");
        transactionDTO.setTransactionDate(LocalDate.now());

        // Then set to null
        transactionDTO.setId(null);
        transactionDTO.setAmount(null);
        transactionDTO.setMerchant(null);
        transactionDTO.setClientName(null);
        transactionDTO.setTransactionDate(null);

        assertNull(transactionDTO.getId());
        assertNull(transactionDTO.getAmount());
        assertNull(transactionDTO.getMerchant());
        assertNull(transactionDTO.getClientName());
        assertNull(transactionDTO.getTransactionDate());
    }

    @Test
    void updateExistingTransaction_shouldWork() {
        // Create initial transaction
        transactionDTO.setId(1L);
        transactionDTO.setAmount(100);
        transactionDTO.setMerchant("Old Store");
        transactionDTO.setClientName("Old Client");
        transactionDTO.setTransactionDate(LocalDate.of(2023, 1, 1));

        // Update all fields
        transactionDTO.setId(2L);
        transactionDTO.setAmount(200);
        transactionDTO.setMerchant("New Store");
        transactionDTO.setClientName("New Client");
        transactionDTO.setTransactionDate(LocalDate.of(2023, 12, 31));

        assertEquals(2L, transactionDTO.getId());
        assertEquals(200, transactionDTO.getAmount());
        assertEquals("New Store", transactionDTO.getMerchant());
        assertEquals("New Client", transactionDTO.getClientName());
        assertEquals(LocalDate.of(2023, 12, 31), transactionDTO.getTransactionDate());
    }

    @Test
    void multipleSameValidationErrors_shouldBeDetected() {
        transactionDTO.setAmount(null); // Required validation error
        transactionDTO.setMerchant(""); // NotBlank validation error
        transactionDTO.setClientName(""); // NotBlank validation error
        transactionDTO.setTransactionDate(null); // NotNull validation error

        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);

        assertTrue(violations.size() >= 4);
    }
}
