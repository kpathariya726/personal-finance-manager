package com.finance.service;

import com.finance.dto.ReportResponse;
import com.finance.entity.CategoryType;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.exception.BadRequestException;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public ReportResponse getMonthlyReport(User user, int year, int month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Invalid month: " + month + ". Month must be between 1 and 12.");
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(user, startDate, endDate);
        return aggregateReport(transactions);
    }

    @Transactional(readOnly = true)
    public ReportResponse getYearlyReport(User user, int year) {
        if (year < 1) {
            throw new BadRequestException("Invalid year: " + year);
        }

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(user, startDate, endDate);
        return aggregateReport(transactions);
    }

    private ReportResponse aggregateReport(List<Transaction> transactions) {
        Map<String, BigDecimal> totalIncome = new LinkedHashMap<>();
        Map<String, BigDecimal> totalExpenses = new LinkedHashMap<>();
        BigDecimal netSavings = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            BigDecimal amount = t.getAmount();

            if (t.getCategory().getType() == CategoryType.INCOME) {
                totalIncome.put(categoryName, totalIncome.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                netSavings = netSavings.add(amount);
            } else if (t.getCategory().getType() == CategoryType.EXPENSE) {
                totalExpenses.put(categoryName, totalExpenses.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                netSavings = netSavings.subtract(amount);
            }
        }

        return new ReportResponse(totalIncome, totalExpenses, netSavings);
    }
}
