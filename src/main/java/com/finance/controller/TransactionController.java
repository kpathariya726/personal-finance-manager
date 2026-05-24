package com.finance.controller;

import com.finance.dto.TransactionCreateRequest;
import com.finance.dto.TransactionListResponse;
import com.finance.dto.TransactionResponse;
import com.finance.dto.TransactionUpdateRequest;
import com.finance.entity.User;
import com.finance.service.TransactionService;
import com.finance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<TransactionListResponse> getTransactions(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "category", required = false) String category) {
        User user = userService.getCurrentUser();
        TransactionListResponse response = transactionService.getTransactions(user, startDate, endDate, category);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        User user = userService.getCurrentUser();
        TransactionResponse response = transactionService.createTransaction(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        User user = userService.getCurrentUser();
        TransactionResponse response = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.ok().build();
    }
}
