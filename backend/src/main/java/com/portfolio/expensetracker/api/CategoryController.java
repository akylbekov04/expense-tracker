package com.portfolio.expensetracker.api;

import com.portfolio.expensetracker.common.statics.Endpoints;
import com.portfolio.expensetracker.dto.CategoryRequest;
import com.portfolio.expensetracker.dto.CategoryResponse;
import com.portfolio.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.CATEGORY_V1_API)
@Tag(name = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Categories", description = "Get all categories",
            operationId = "getCategories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create new category",
            operationId = "createCategory")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.accepted().body(categoryService.create(request));
    }
}
