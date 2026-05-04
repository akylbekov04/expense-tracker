package com.portfolio.expensetracker.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.portfolio.expensetracker.dto.ExpenseResponse;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExportService {

    public byte[] exportCsv(List<ExpenseResponse> expenses) {
        StringBuilder builder = new StringBuilder("Date,Title,Category,Amount,Note\n");
        for (ExpenseResponse expense : expenses) {
            builder.append(expense.expenseDate()).append(',')
                    .append(escape(expense.title())).append(',')
                    .append(escape(expense.category().name())).append(',')
                    .append(expense.amount()).append(',')
                    .append(escape(expense.note()))
                    .append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportPdf(List<ExpenseResponse> expenses, LocalDate startDate, LocalDate endDate) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("Expense Report"));
            document.add(new Paragraph("Period: " + startDate + " to " + endDate));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            addCell(table, "Date");
            addCell(table, "Title");
            addCell(table, "Category");
            addCell(table, "Amount");

            for (ExpenseResponse expense : expenses) {
                addCell(table, expense.expenseDate().toString());
                addCell(table, expense.title());
                addCell(table, expense.category().name());
                addCell(table, expense.amount().toPlainString());
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            log.error("ExportService.exportPdf() throws: {}", e.getMessage());
            throw new IllegalStateException("Failed to create PDF", e);
        }
    }

    private void addCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Paragraph(value == null ? "" : value)));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replace("\"", "\"\"");
        return "\"" + sanitized + "\"";
    }
}
