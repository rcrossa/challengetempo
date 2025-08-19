package com.tempo.challenge.service;

import com.tempo.challenge.dtos.TransactionDTO;
import com.tempo.challenge.exception.ClientTransactionLimitException;
import com.tempo.challenge.exception.TransactionNotFoundException;
import com.tempo.challenge.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTransactions_returnsAll() {
        TransactionDTO t1 = new TransactionDTO();
        t1.setId(1L);
        t1.setAmount(100);
        t1.setMerchant("M1");
        t1.setClientName("C1");
        t1.setTransactionDate(LocalDate.now());
        TransactionDTO t2 = new TransactionDTO();
        t2.setId(2L);
        t2.setAmount(200);
        t2.setMerchant("M2");
        t2.setClientName("C2");
        t2.setTransactionDate(LocalDate.now());
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));
        List<TransactionDTO> result = transactionService.getAllTransactions();
        assertEquals(2, result.size());
        assertEquals("C1", result.get(0).getClientName());
    }

    @Test
    void getTransactionById_found() {
        TransactionDTO t = new TransactionDTO();
        t.setId(1L);
        t.setAmount(100);
        t.setMerchant("M1");
        t.setClientName("C1");
        t.setTransactionDate(LocalDate.now());
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(t));
        Optional<TransactionDTO> result = transactionService.getTransactionById(1L);
        assertTrue(result.isPresent());
        assertEquals("C1", result.get().getClientName());
    }

    @Test
    void getTransactionById_notFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<TransactionDTO> result = transactionService.getTransactionById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void createTransaction_success() {
        TransactionDTO t = new TransactionDTO();
        t.setAmount(100);
        t.setMerchant("M1");
        t.setClientName("C1");
        when(transactionRepository.countByClientName("C1")).thenReturn(99L);
        when(transactionRepository.save(any(TransactionDTO.class))).thenAnswer(invocation -> {
            TransactionDTO arg = invocation.getArgument(0);
            arg.setId(1L);
            return arg;
        });
        TransactionDTO result = transactionService.createTransaction(t);
        assertNotNull(result.getId());
        assertEquals("C1", result.getClientName());
        assertEquals(LocalDate.now(), result.getTransactionDate());
    }

    @Test
    void createTransaction_clientLimitExceeded() {
        TransactionDTO t = new TransactionDTO();
        t.setAmount(100);
        t.setMerchant("M1");
        t.setClientName("C1");
        when(transactionRepository.countByClientName("C1")).thenReturn(100L);
        assertThrows(ClientTransactionLimitException.class, () -> transactionService.createTransaction(t));
    }

    @Test
    void updateTransaction_success() {
        TransactionDTO existing = new TransactionDTO();
        existing.setId(1L);
        existing.setAmount(100);
        existing.setMerchant("M1");
        existing.setClientName("C1");
        existing.setTransactionDate(LocalDate.now());
        TransactionDTO update = new TransactionDTO();
        update.setAmount(200);
        update.setMerchant("M2");
        update.setClientName("C2");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any(TransactionDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TransactionDTO result = transactionService.updateTransaction(1L, update);
        assertEquals(200, result.getAmount());
        assertEquals("M2", result.getMerchant());
        assertEquals("C2", result.getClientName());
        assertEquals(LocalDate.now(), result.getTransactionDate());
    }

    @Test
    void updateTransaction_notFound() {
        TransactionDTO update = new TransactionDTO();
        update.setAmount(200);
        update.setMerchant("M2");
        update.setClientName("C2");
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class, () -> transactionService.updateTransaction(1L, update));
    }

    @Test
    void deleteTransaction_success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(1L);
        assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void deleteTransaction_notFound() {
        when(transactionRepository.existsById(1L)).thenReturn(false);
        assertThrows(TransactionNotFoundException.class, () -> transactionService.deleteTransaction(1L));
    }
}