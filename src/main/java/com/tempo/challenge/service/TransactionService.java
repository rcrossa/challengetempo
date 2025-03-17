package com.tempo.challenge.service;

import com.tempo.challenge.exception.ClientTransactionLimitException;
import com.tempo.challenge.exception.TransactionNotFoundException;
import com.tempo.challenge.dtos.TransactionDTO;
import com.tempo.challenge.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        if (transactionRepository.countByClientName(transactionDTO.getClientName()) >= 100) {
            throw new ClientTransactionLimitException("The client has reached the maximum number of transactions");
        }
        transactionDTO.setTransactionDate(LocalDate.now());
        return transactionRepository.save(transactionDTO);
    }

    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        return transactionRepository.findById(id).map(existingTransactionDTO -> {
            existingTransactionDTO.setAmount(transactionDTO.getAmount());
            existingTransactionDTO.setMerchant(transactionDTO.getMerchant());
            existingTransactionDTO.setClientName(transactionDTO.getClientName());
            existingTransactionDTO.setTransactionDate(LocalDate.now());
            return transactionRepository.save(existingTransactionDTO);
        }).orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    @Transactional
    public void deleteTransaction(Long id) {
      if(!transactionRepository.existsById(id)) {
        throw new TransactionNotFoundException("Transaction not found");
      }
        transactionRepository.deleteById(id);
    }
}

