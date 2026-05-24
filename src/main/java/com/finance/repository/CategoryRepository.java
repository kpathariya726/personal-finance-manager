package com.finance.repository;

import com.finance.entity.Category;
import com.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.custom = false OR c.user = :user")
    List<Category> findAllVisibleToUser(@Param("user") User user);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND (c.custom = false OR c.user = :user)")
    Optional<Category> findByNameAndUser(@Param("name") String name, @Param("user") User user);

    Optional<Category> findByNameAndCustomTrueAndUser(String name, User user);

    Optional<Category> findByNameAndCustomFalse(String name);
}
