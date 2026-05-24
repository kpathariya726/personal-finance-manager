package com.finance.service;

import com.finance.dto.TransactionCreateRequest;
import com.finance.dto.TransactionListResponse;
import com.finance.dto.TransactionResponse;
import com.finance.dto.TransactionUpdateRequest;
import com.finance.entity.Category;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.exception.BadRequestException;
import com.finance.exception.ForbiddenException;
import com.finance.exception.NotFoundException;
import com.finance.repository.CategoryRepository;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public TransactionListResponse getTransactions(User user, LocalDate startDate, LocalDate endDate, String categoryName) {
        List<TransactionResponse> list = transactionRepository.filterTransactions(user, startDate, endDate, categoryName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new TransactionListResponse(list);
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request, User user) {
        Category category = categoryRepository.findByNameAndUser(request.getCategory(), user)
                .orElseThrow(() -> new BadRequestException("Category not found: " + request.getCategory()));

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getDate(),
                category,
                request.getDescription(),
                user
        );

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this transaction");
        }

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findByNameAndUser(request.getCategory(), user)
                    .orElseThrow(() -> new BadRequestException("Category not found: " + request.getCategory()));
            transaction.setCategory(category);
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        // Date field is strictly IGNORED during updates as per specification rules.

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }

    public TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getCategory().getType().name()
        );
    }
}
