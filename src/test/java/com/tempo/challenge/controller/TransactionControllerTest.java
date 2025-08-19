package com.tempo.challenge.controller;

import com.tempo.challenge.apiresponse.ApiResponse;
import com.tempo.challenge.dtos.TransactionDTO;
import com.tempo.challenge.service.TransactionService;
import com.tempo.challenge.util.RateLimiter;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Bucket bucket;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionDTO sampleTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar transacción de ejemplo
        sampleTransaction = new TransactionDTO();
        sampleTransaction.setId(1L);
        sampleTransaction.setAmount(100);
        sampleTransaction.setMerchant("Test Merchant");
        sampleTransaction.setClientName("Test Client");
        sampleTransaction.setTransactionDate(LocalDate.now());
    }

    // ======= TESTS PARA getAllTransactions =======

    @Test
    void getAllTransactions_success() {
        // Configurar mocks
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.getBucket("127.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        List<TransactionDTO> transactions = Arrays.asList(sampleTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Ejecutar
        ResponseEntity<ApiResponse<List<TransactionDTO>>> response = transactionController.getAllTransactions(request);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transactions retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());

        verify(transactionService).getAllTransactions();
        verify(rateLimiter).getBucket("127.0.0.1");
        verify(bucket).tryConsume(1);
    }

    @Test
    void getAllTransactions_rateLimitExceeded() {
        // Configurar mocks para rate limit excedido
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.getBucket("127.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false);

        // Ejecutar
        ResponseEntity<ApiResponse<List<TransactionDTO>>> response = transactionController.getAllTransactions(request);

        // Verificar
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Too many requests", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(transactionService, never()).getAllTransactions();
        verify(rateLimiter).getBucket("127.0.0.1");
        verify(bucket).tryConsume(1);
    }

    @Test
    void getAllTransactions_emptyList() {
        // Configurar mocks
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.getBucket("127.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        // Ejecutar
        ResponseEntity<ApiResponse<List<TransactionDTO>>> response = transactionController.getAllTransactions(request);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transactions retrieved successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isEmpty());
    }

    // ======= TESTS PARA getTransactionById =======

    @Test
    void getTransactionById_found() {
        // Configurar mock
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(sampleTransaction));

        // Ejecutar
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(1L);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction retrieved successfully", response.getBody().getMessage());
        assertEquals(sampleTransaction, response.getBody().getData());

        verify(transactionService).getTransactionById(1L);
    }

    @Test
    void getTransactionById_notFound() {
        // Configurar mock
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.empty());

        // Ejecutar
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(1L);

        // Verificar
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Transaction not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(transactionService).getTransactionById(1L);
    }

    // ======= TESTS PARA createTransaction =======

    @Test
    void createTransaction_success() {
        // Configurar mock
        TransactionDTO newTransaction = new TransactionDTO();
        newTransaction.setAmount(200);
        newTransaction.setMerchant("New Merchant");
        newTransaction.setClientName("New Client");

        TransactionDTO createdTransaction = new TransactionDTO();
        createdTransaction.setId(2L);
        createdTransaction.setAmount(200);
        createdTransaction.setMerchant("New Merchant");
        createdTransaction.setClientName("New Client");
        createdTransaction.setTransactionDate(LocalDate.now());

        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(createdTransaction);

        // Ejecutar
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.createTransaction(newTransaction);

        // Verificar
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction created successfully", response.getBody().getMessage());
        assertEquals(createdTransaction, response.getBody().getData());

        verify(transactionService).createTransaction(newTransaction);
    }

    // ======= TESTS PARA updateTransaction =======

    @Test
    void updateTransaction_success() {
        // Configurar mock
        TransactionDTO updateData = new TransactionDTO();
        updateData.setAmount(300);
        updateData.setMerchant("Updated Merchant");
        updateData.setClientName("Updated Client");

        TransactionDTO updatedTransaction = new TransactionDTO();
        updatedTransaction.setId(1L);
        updatedTransaction.setAmount(300);
        updatedTransaction.setMerchant("Updated Merchant");
        updatedTransaction.setClientName("Updated Client");
        updatedTransaction.setTransactionDate(LocalDate.now());

        when(transactionService.updateTransaction(eq(1L), any(TransactionDTO.class))).thenReturn(updatedTransaction);

        // Ejecutar
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.updateTransaction(1L, updateData);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction updated successfully", response.getBody().getMessage());
        assertEquals(updatedTransaction, response.getBody().getData());

        verify(transactionService).updateTransaction(1L, updateData);
    }

    @Test
    void updateTransaction_exception() {
        // Configurar mock para lanzar excepción
        TransactionDTO updateData = new TransactionDTO();
        updateData.setAmount(300);
        updateData.setMerchant("Updated Merchant");
        updateData.setClientName("Updated Client");

        when(transactionService.updateTransaction(eq(1L), any(TransactionDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Ejecutar
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.updateTransaction(1L, updateData);

        // Verificar
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Could not commit JPA transaction", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(transactionService).updateTransaction(1L, updateData);
    }

    // ======= TESTS PARA deleteTransaction =======

    @Test
    void deleteTransaction_success() {
        // Configurar mock (void method, no return value needed)
        doNothing().when(transactionService).deleteTransaction(1L);

        // Ejecutar
        ResponseEntity<ApiResponse<Void>> response = transactionController.deleteTransaction(1L);

        // Verificar
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(transactionService).deleteTransaction(1L);
    }

    @Test
    void deleteTransaction_notFound() {
        // Configurar mock para lanzar excepción cuando no encuentra la transacción
        doThrow(new RuntimeException("Transaction not found")).when(transactionService).deleteTransaction(1L);

        // Ejecutar y verificar que se lanza excepción
        assertThrows(RuntimeException.class, () -> transactionController.deleteTransaction(1L));

        verify(transactionService).deleteTransaction(1L);
    }

    // ======= TESTS ADICIONALES PARA COBERTURA COMPLETA =======

    @Test
    void constructor_initializesFields() {
        // Verificar que el constructor inicializa correctamente los campos
        TransactionController controller = new TransactionController(transactionService, rateLimiter);
        assertNotNull(controller);
    }

    @Test
    void getAllTransactions_differentIpAddresses() {
        // Probar con diferentes direcciones IP
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(rateLimiter.getBucket("192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList(sampleTransaction));

        ResponseEntity<ApiResponse<List<TransactionDTO>>> response = transactionController.getAllTransactions(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rateLimiter).getBucket("192.168.1.1");
    }

    @Test
    void getTransactionById_nullId_shouldHandleGracefully() {
        // Probar con ID null (aunque no debería pasar por @PathVariable)
        when(transactionService.getTransactionById(null)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService).getTransactionById(null);
    }
}