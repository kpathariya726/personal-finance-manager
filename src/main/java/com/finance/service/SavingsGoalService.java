package com.finance.service;

import com.finance.dto.SavingsGoalCreateRequest;
import com.finance.dto.SavingsGoalListResponse;
import com.finance.dto.SavingsGoalResponse;
import com.finance.dto.SavingsGoalUpdateRequest;
import com.finance.entity.CategoryType;
import com.finance.entity.SavingsGoal;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.exception.BadRequestException;
import com.finance.exception.ForbiddenException;
import com.finance.exception.NotFoundException;
import com.finance.repository.SavingsGoalRepository;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public SavingsGoalListResponse getAllGoals(User user) {
        List<SavingsGoalResponse> list = savingsGoalRepository.findByUser(user).stream()
                .map(goal -> mapToResponse(goal, user))
                .collect(Collectors.toList());
        return new SavingsGoalListResponse(list);
    }

    @Transactional(readOnly = true)
    public SavingsGoalResponse getGoalById(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savings goal not found: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to view this savings goal");
        }

        return mapToResponse(goal, user);
    }

    @Transactional
    public SavingsGoalResponse createGoal(SavingsGoalCreateRequest request, User user) {
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        if (startDate.isAfter(request.getTargetDate())) {
            throw new BadRequestException("Start date cannot be after target date");
        }

        SavingsGoal goal = new SavingsGoal(
                request.getGoalName(),
                request.getTargetAmount(),
                request.getTargetDate(),
                startDate,
                user
        );

        SavingsGoal saved = savingsGoalRepository.save(goal);
        return mapToResponse(saved, user);
    }

    @Transactional
    public SavingsGoalResponse updateGoal(Long id, SavingsGoalUpdateRequest request, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savings goal not found: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this savings goal");
        }

        LocalDate newStart = request.getStartDate() != null ? request.getStartDate() : goal.getStartDate();
        LocalDate newTarget = request.getTargetDate() != null ? request.getTargetDate() : goal.getTargetDate();
        if (newStart.isAfter(newTarget)) {
            throw new BadRequestException("Start date cannot be after target date");
        }

        if (request.getGoalName() != null) {
            goal.setGoalName(request.getGoalName());
        }

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        }

        SavingsGoal saved = savingsGoalRepository.save(goal);
        return mapToResponse(saved, user);
    }

    @Transactional
    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savings goal not found: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to delete this savings goal");
        }

        savingsGoalRepository.delete(goal);
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal, User user) {
        // Calculate progress dynamically
        List<Transaction> transactions = transactionRepository.findByUserAndDateGreaterThanEqual(user, goal.getStartDate());
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getCategory().getType() == CategoryType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getCategory().getType() == CategoryType.EXPENSE) {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }

        BigDecimal progress = totalIncome.subtract(totalExpense);
        BigDecimal remaining = goal.getTargetAmount().subtract(progress);

        double pct = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            pct = (progress.doubleValue() / goal.getTargetAmount().doubleValue()) * 100.0;
            pct = Math.round(pct * 100.0) / 100.0; // round to 2 decimal places
        }

        return new SavingsGoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                progress,
                pct,
                remaining
        );
    }
}
