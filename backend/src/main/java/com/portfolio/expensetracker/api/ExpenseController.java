package com.portfolio.expensetracker.api;

import com.portfolio.expensetracker.common.statics.Endpoints;
import com.portfolio.expensetracker.dto.ExpenseRequest;
import com.portfolio.expensetracker.dto.ExpenseResponse;
import com.portfolio.expensetracker.service.ExpenseService;
import com.portfolio.expensetracker.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.EXPENSE_V1_API)
@Tag(name = "Expense API")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExportService exportService;

    public ExpenseController(ExpenseService expenseService, ExportService exportService) {
        this.expenseService = expenseService;
        this.exportService = exportService;
    }

    @GetMapping
    @Operation(summary = "Expenses", description = "Get all expenses",
            operationId = "getExpenses")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getExpenses(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "Create expense", description = "Create new expense",
            operationId = "createExpense")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.accepted().body(expenseService.createExpense(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Delete expense",
            operationId = "deleteExpense")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    @Operation(summary = "Export CSV", description = "Export in CSV format",
            operationId = "exportCsv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ExpenseResponse> expenses = expenseService.getExpenses(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses.csv")
                .body(exportService.exportCsv(expenses));
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Export PDF", description = "Export in PDF format",
            operationId = "exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ExpenseResponse> expenses = expenseService.getExpenses(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses.pdf")
                .body(exportService.exportPdf(expenses, startDate, endDate));
    }
}
