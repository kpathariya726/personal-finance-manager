package com.finance.controller;

import com.finance.dto.SavingsGoalCreateRequest;
import com.finance.dto.SavingsGoalListResponse;
import com.finance.dto.SavingsGoalResponse;
import com.finance.dto.SavingsGoalUpdateRequest;
import com.finance.entity.User;
import com.finance.service.SavingsGoalService;
import com.finance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goals")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<SavingsGoalListResponse> getGoals() {
        User user = userService.getCurrentUser();
        SavingsGoalListResponse response = savingsGoalService.getAllGoals(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoal(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.getGoalById(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@Valid @RequestBody SavingsGoalCreateRequest request) {
        User user = userService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.createGoal(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @PathVariable("id") Long id,
            @Valid @RequestBody SavingsGoalUpdateRequest request) {
        User user = userService.getCurrentUser();
        SavingsGoalResponse response = savingsGoalService.updateGoal(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        savingsGoalService.deleteGoal(id, user);
        return ResponseEntity.ok().build();
    }
}
