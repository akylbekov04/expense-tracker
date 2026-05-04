package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.Expense;
import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.dto.CategoryTotalResponse;
import com.portfolio.expensetracker.dto.ReportResponse;
import com.portfolio.expensetracker.dto.TrendPointResponse;
import com.portfolio.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final UserContextService userContextService;

    public ReportService(ExpenseRepository expenseRepository,
                         UserContextService userContextService) {
        this.expenseRepository = expenseRepository;
        this.userContextService = userContextService;
    }

    public ReportResponse monthlyReport(int year, int month) {
        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        return buildReport("MONTH", startDate, endDate);
    }

    public ReportResponse weeklyReport(LocalDate date) {
        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);
        return buildReport("WEEK", startDate, endDate);
    }

    private ReportResponse buildReport(String period, LocalDate startDate, LocalDate endDate) {
        User user = userContextService.getCurrentUser();
        List<Expense> expenses = expenseRepository
                .findAllByUserAndExpenseDateBetweenOrderByExpenseDateAsc(user, startDate, endDate);

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, CategoryTotalResponse> byCategoryMap = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> trendMap = new LinkedHashMap<>();

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            trendMap.put(cursor, BigDecimal.ZERO);
            cursor = cursor.plusDays(1);
        }

        expenses.forEach(expense -> {
            String key = expense.getCategory().getName();
            CategoryTotalResponse existing = byCategoryMap.get(key);
            BigDecimal nextTotal = (existing == null ? BigDecimal.ZERO : existing.total()).add(expense.getAmount());
            byCategoryMap.put(key, new CategoryTotalResponse(key, expense.getCategory().getColor(), nextTotal));
            trendMap.put(expense.getExpenseDate(), trendMap.get(expense.getExpenseDate()).add(expense.getAmount()));
        });

        List<CategoryTotalResponse> byCategory = byCategoryMap.values().stream()
                .sorted(Comparator.comparing(CategoryTotalResponse::total).reversed())
                .toList();

        List<TrendPointResponse> trend = trendMap.entrySet().stream()
                .map(entry -> new TrendPointResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new ReportResponse(period, startDate, endDate, total, byCategory, trend);
    }
}
