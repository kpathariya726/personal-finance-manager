package com.finance.service;

import com.finance.dto.CategoryRequest;
import com.finance.dto.CategoryResponse;
import com.finance.entity.Category;
import com.finance.entity.CategoryType;
import com.finance.entity.User;
import com.finance.exception.BadRequestException;
import com.finance.exception.ConflictException;
import com.finance.exception.NotFoundException;
import com.finance.repository.CategoryRepository;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllVisibleCategories(User user) {
        return categoryRepository.findAllVisibleToUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCustomCategory(CategoryRequest request, User user) {
        // Enforce exact case-insensitive or exact uniqueness per user
        Optional<Category> existing = categoryRepository.findByNameAndUser(request.getName(), user);
        if (existing.isPresent()) {
            throw new ConflictException("Category already exists: " + request.getName());
        }

        CategoryType type;
        try {
            type = CategoryType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid category type: " + request.getType());
        }

        Category category = new Category(
                request.getName(),
                type,
                true, // Custom category
                user
        );

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteCategoryByName(String name, User user) {
        // 1. Try to find if it is a custom category of the current user
        Optional<Category> customCategoryOpt = categoryRepository.findByNameAndCustomTrueAndUser(name, user);
        
        if (customCategoryOpt.isPresent()) {
            Category category = customCategoryOpt.get();
            // Check if in use
            long count = transactionRepository.countByCategoryAndUser(category, user);
            if (count > 0) {
                throw new ConflictException("Category is in use by transactions and cannot be deleted: " + name);
            }
            categoryRepository.delete(category);
            return;
        }

        // 2. If it's a default category, throw BadRequest/Conflict (must be 4xx)
        Optional<Category> defaultCategoryOpt = categoryRepository.findByNameAndCustomFalse(name);
        if (defaultCategoryOpt.isPresent()) {
            throw new BadRequestException("Default categories cannot be deleted: " + name);
        }

        // 3. Otherwise, it is not found
        throw new NotFoundException("Category not found: " + name);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType().name(),
                category.isCustom()
        );
    }
}
