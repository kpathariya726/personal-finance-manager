package com.finance.repository;

import com.finance.entity.Category;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:category IS NULL OR t.category.name = :category) " +
           "ORDER BY t.date DESC, t.id DESC")
    List<Transaction> filterTransactions(
        @Param("user") User user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("category") String category
    );

    long countByCategoryAndUser(Category category, User user);

    List<Transaction> findByUserAndDateGreaterThanEqual(User user, LocalDate startDate);

    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    Optional<Transaction> findByIdAndUser(Long id, User user);
}
