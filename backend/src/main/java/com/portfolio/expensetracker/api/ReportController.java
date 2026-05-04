package com.portfolio.expensetracker.api;

import com.portfolio.expensetracker.common.statics.Endpoints;
import com.portfolio.expensetracker.dto.ReportResponse;
import com.portfolio.expensetracker.service.ReportService;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.REPORT_V1_API)
@Tag(name = "ReportController")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    @Operation(summary = "Monthly report", description = "Monthly report",
            operationId = "monthly")
    public ResponseEntity<ReportResponse> monthly(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.monthlyReport(year, month));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Weekly report", description = "Weekly report",
            operationId = "weekly")
    public ResponseEntity<ReportResponse> weekly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(reportService.weeklyReport(date));
    }
}
