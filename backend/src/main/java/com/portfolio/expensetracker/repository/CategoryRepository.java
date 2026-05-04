package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.model.Category;
import com.portfolio.expensetracker.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserOrderByNameAsc(User user);

    Optional<Category> findByIdAndUser(Long id, User user);
}
