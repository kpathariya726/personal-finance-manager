package com.finance.controller;

import com.finance.dto.CategoryRequest;
import com.finance.dto.CategoryResponse;
import com.finance.entity.User;
import com.finance.service.CategoryService;
import com.finance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        User user = userService.getCurrentUser();
        List<CategoryResponse> categories = categoryService.getAllVisibleCategories(user);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        User user = userService.getCurrentUser();
        CategoryResponse response = categoryService.createCustomCategory(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCategory(@PathVariable("name") String name) {
        User user = userService.getCurrentUser();
        categoryService.deleteCategoryByName(name, user);
        return ResponseEntity.ok().build();
    }
}
