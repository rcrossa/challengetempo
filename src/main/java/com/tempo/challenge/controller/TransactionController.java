package com.tempo.challenge.controller;

import com.tempo.challenge.apiresponse.ApiResponse;
import com.tempo.challenge.service.TransactionService;
import com.tempo.challenge.util.RateLimiter;
import com.tempo.challenge.dtos.TransactionDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@CrossOrigin(origins = "http://10.19.8.117:3000")
@RequestMapping("/api")
@Tag(name = "Transactions", description = "Operations related to transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final RateLimiter rateLimiter;

    public TransactionController(TransactionService transactionService, RateLimiter rateLimiter) {
        this.transactionService = transactionService;
        this.rateLimiter = rateLimiter;
    }


    @Operation(summary = "Get all transactions", description = "Return all transactions")
    @GetMapping(value = "transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAllTransactions(HttpServletRequest request) {
        if(!rateLimiter.getBucket(request.getRemoteAddr()).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse<>(false, "Too many requests", null));
        }
        List<TransactionDTO> transactionDTOS = transactionService.getAllTransactions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Transactions retrieved successfully", transactionDTOS));
    }

    @Operation(summary = "Get transaction by id", description = "Return a transaction by id")
    @GetMapping(value = "/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable Long id) {
        Optional<TransactionDTO> transaction = transactionService.getTransactionById(id);
        return transaction.map(value -> ResponseEntity.ok(new ApiResponse<>(true, "Transaction retrieved successfully", value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Transaction not found", null)));
    }

    @Operation(summary = "Create a transaction", description = "Create a transaction")
    @PostMapping(value = "/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO createdTransactionDTO = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Transaction created successfully", createdTransactionDTO));
    }

    @Operation(summary = "Update a transaction", description = "Update a transaction")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO updatedTransactionDTO = transactionService.updateTransaction(id, transactionDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Transaction updated successfully", updatedTransactionDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Could not commit JPA transaction", null));
        }
    }

    @Operation(summary = "Delete a transaction", description = "Delete a transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(true, "Transaction deleted successfully", null));
    }
}