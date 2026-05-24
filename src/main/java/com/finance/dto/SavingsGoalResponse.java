package com.finance.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class SavingsGoalResponse {

    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private LocalDate startDate;
    private BigDecimal currentProgress;
    private double progressPercentage;
    private BigDecimal remainingAmount;

    public SavingsGoalResponse() {
    }

    public SavingsGoalResponse(Long id, String goalName, BigDecimal targetAmount, LocalDate targetDate,
                               LocalDate startDate, BigDecimal currentProgress, double progressPercentage,
                               BigDecimal remainingAmount) {
        this.id = id;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.startDate = startDate;
        this.currentProgress = currentProgress;
        this.progressPercentage = progressPercentage;
        this.remainingAmount = remainingAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount != null ? targetAmount.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getCurrentProgress() {
        if (currentProgress == null || currentProgress.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(0);
        }
        return currentProgress.setScale(2, RoundingMode.HALF_UP);
    }

    public void setCurrentProgress(BigDecimal currentProgress) {
        this.currentProgress = currentProgress;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount != null ? remainingAmount.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
