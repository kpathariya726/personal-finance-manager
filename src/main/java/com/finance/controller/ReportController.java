package com.finance.controller;

import com.finance.dto.ReportResponse;
import com.finance.entity.User;
import com.finance.service.ReportService;
import com.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<ReportResponse> getMonthlyReport(
            @PathVariable("year") int year,
            @PathVariable("month") int month) {
        User user = userService.getCurrentUser();
        ReportResponse response = reportService.getMonthlyReport(user, year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<ReportResponse> getYearlyReport(@PathVariable("year") int year) {
        User user = userService.getCurrentUser();
        ReportResponse response = reportService.getYearlyReport(user, year);
        return ResponseEntity.ok(response);
    }
}
