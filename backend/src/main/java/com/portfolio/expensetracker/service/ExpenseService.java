package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.Category;
import com.portfolio.expensetracker.model.Expense;
import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.dto.CategoryResponse;
import com.portfolio.expensetracker.dto.ExpenseRequest;
import com.portfolio.expensetracker.dto.ExpenseResponse;
import com.portfolio.expensetracker.common.exception.ApiException;
import com.portfolio.expensetracker.repository.ExpenseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private static final Logger log = Logger.getLogger(ExpenseService.class.getName());

    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final UserContextService userContextService;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryService categoryService,
                          UserContextService userContextService) {
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
        this.userContextService = userContextService;
    }

    public List<ExpenseResponse> getExpenses(LocalDate startDate, LocalDate endDate) {
        User user = userContextService.getCurrentUser();
        return expenseRepository.findAllByUserAndExpenseDateBetweenOrderByExpenseDateAsc(user, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        log.info(String.format("ExpenseService.createExpense(%s) started process", request.title()));
        User user = userContextService.getCurrentUser();
        Category category = categoryService.findEntity(request.categoryId());

        Expense expense = new Expense();
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setTitle(request.title().trim());
        expense.setNote(request.note());
        expense.setCategory(category);
        expense.setUser(user);
        ExpenseResponse expenseResponse = toResponse(expenseRepository.save(expense));
        log.info(String.format("ExpenseService.createExpense(%s) successfully created expense", request.title()));
        return expenseResponse;
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findByIdAndUser(id, userContextService.getCurrentUser())
                .orElseThrow(() -> {
                    log.severe(String.format("ExpenseService.deleteExpense(%d) couldn't find expense", id));
                    return new ApiException(HttpStatus.NOT_FOUND, "Expense not found");
                });
        expenseRepository.delete(expense);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getTitle(),
                expense.getNote(),
                new CategoryResponse(
                        expense.getCategory().getId(),
                        expense.getCategory().getName(),
                        expense.getCategory().getColor()
                )
        );
    }
}
