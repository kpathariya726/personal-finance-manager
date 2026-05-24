package com.finance.config;

import com.finance.entity.Category;
import com.finance.entity.CategoryType;
import com.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        Map<String, CategoryType> defaultCategories = new HashMap<>();
        
        // Income Default Categories
        defaultCategories.put("Salary", CategoryType.INCOME);
        
        // Expense Default Categories
        defaultCategories.put("Food", CategoryType.EXPENSE);
        defaultCategories.put("Rent", CategoryType.EXPENSE);
        defaultCategories.put("Transportation", CategoryType.EXPENSE);
        defaultCategories.put("Entertainment", CategoryType.EXPENSE);
        defaultCategories.put("Healthcare", CategoryType.EXPENSE);
        defaultCategories.put("Utilities", CategoryType.EXPENSE);

        for (Map.Entry<String, CategoryType> entry : defaultCategories.entrySet()) {
            String name = entry.getKey();
            CategoryType type = entry.getValue();

            if (categoryRepository.findByNameAndCustomFalse(name).isEmpty()) {
                Category defaultCategory = new Category(name, type, false, null);
                categoryRepository.save(defaultCategory);
            }
        }
    }
}
