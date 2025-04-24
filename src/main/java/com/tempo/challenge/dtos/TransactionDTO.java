package com.tempo.challenge.dtos;

import com.tempo.challenge.validation.ValidString;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "transactions")
public class TransactionDTO {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "The amount is required")
    @Min(value = 1, message = "The amount must be greater than 0")
    private Integer amount;


    @NotBlank(message = "The merchant is required")
    @ValidString(message = "The merchant must contain only letters, numbers, and spaces")
    private String merchant;

    @NotBlank(message = "The name of the client is required")
    @ValidString(message = "The client name must contain only letters, numbers, and spaces")
    private String clientName;

    @NotNull(message = "The date of the transaction is required")
    @PastOrPresent(message = "The date of the transaction must be in the past or present")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate transactionDate;

    public Long getId() {
        return id;
    }

    public @NotNull(message = "The amount is required") @Min(value = 1, message = "The amount must be greater than 0") Integer getAmount() {
        return amount;
    }

    public @NotBlank(message = "The merchant is required") String getMerchant() {
        return merchant;
    }

    public @NotBlank(message = "The name of the client is required") String getClientName() {
        return clientName;
    }

    public @NotNull(message = "The date of the transaction is required") @PastOrPresent(message = "The date of the transaction must be in the past or present") LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(@NotNull(message = "The amount is required") @Min(value = 1, message = "The amount must be greater than 0") Integer amount) {
        this.amount = amount;
    }

    public void setMerchant(@NotBlank(message = "The merchant is required") String merchant) {
        this.merchant = merchant;
    }

    public void setClientName(@NotBlank(message = "The name of the client is required") String clientName) {
        this.clientName = clientName;
    }

    public void setTransactionDate(@NotNull(message = "The date of the transaction is required") @PastOrPresent(message = "The date of the transaction must be in the past or present") LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}