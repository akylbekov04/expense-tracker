package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.model.Expense;
import com.portfolio.expensetracker.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUserAndExpenseDateBetweenOrderByExpenseDateAsc(
            User user, LocalDate startDate, LocalDate endDate);

    Optional<Expense> findByIdAndUser(Long id, User user);
}
