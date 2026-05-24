package com.finance.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportResponse {

    private Map<String, BigDecimal> totalIncome;
    private Map<String, BigDecimal> totalExpenses;
    private BigDecimal netSavings;

    public ReportResponse() {
    }

    public ReportResponse(Map<String, BigDecimal> totalIncome, Map<String, BigDecimal> totalExpenses, BigDecimal netSavings) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
    }

    public Map<String, BigDecimal> getTotalIncome() {
        if (totalIncome == null) return null;
        Map<String, BigDecimal> formatted = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : totalIncome.entrySet()) {
            formatted.put(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP));
        }
        return formatted;
    }

    public void setTotalIncome(Map<String, BigDecimal> totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Map<String, BigDecimal> getTotalExpenses() {
        if (totalExpenses == null) return null;
        Map<String, BigDecimal> formatted = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : totalExpenses.entrySet()) {
            formatted.put(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP));
        }
        return formatted;
    }

    public void setTotalExpenses(Map<String, BigDecimal> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetSavings() {
        if (netSavings == null || netSavings.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(0);
        }
        return netSavings.setScale(2, RoundingMode.HALF_UP);
    }

    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }
}
