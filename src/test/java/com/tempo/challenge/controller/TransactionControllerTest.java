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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void getAllTransactions() {
//        Bucket mockBucket = mock(Bucket.class);
//        when(rateLimiter.getBucket(anyString())).thenReturn(mockBucket);
//        when(mockBucket.tryConsume(1)).thenReturn(true);
//        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());
//
//        ResponseEntity<ApiResponse<List<TransactionDTO>>> response = transactionController.getAllTransactions(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(true, response.getBody().isSuccess());
//    }

    @Test
    void getTransactionById() {
        TransactionDTO transactionDTO = new TransactionDTO();
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(transactionDTO));

        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void createTransaction() {
        TransactionDTO transactionDTO = new TransactionDTO();
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transactionDTO);

        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.createTransaction(transactionDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void updateTransaction() {
        TransactionDTO transactionDTO = new TransactionDTO();
        when(transactionService.updateTransaction(eq(1L), any(TransactionDTO.class))).thenReturn(transactionDTO);

        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.updateTransaction(1L, transactionDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void deleteTransaction() {
        doNothing().when(transactionService).deleteTransaction(1L);

        ResponseEntity<ApiResponse<Void>> response = transactionController.deleteTransaction(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }
}