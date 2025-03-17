package com.tempo.challenge.repository;

import com.tempo.challenge.dtos.TransactionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionDTO, Long> {
    List<TransactionDTO> findByClientName(String clientName);
    long countByClientName(String clientName);
}
