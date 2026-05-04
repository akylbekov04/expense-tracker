package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.model.Category;
import com.portfolio.expensetracker.model.User;
import com.portfolio.expensetracker.dto.CategoryRequest;
import com.portfolio.expensetracker.dto.CategoryResponse;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.CategoryRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserContextService userContextService;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByUserOrderByNameAsc(userContextService.getCurrentUser())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        log.info("CategoryService.create({}) started process", request.name());
        User user = userContextService.getCurrentUser();
        Category category = new Category();
        category.setName(request.name().strip());
        category.setColor(request.color());
        category.setUser(user);
        CategoryResponse categoryResponse = toResponse(categoryRepository.save(category));
        log.info("CategoryService.create({}) successfully created category", request.name());
        return categoryResponse;
    }

    public Category findEntity(Long id) {
        return categoryRepository.findByIdAndUser(id, userContextService.getCurrentUser())
                .orElseThrow(() -> {
                    log.error("CategoryService.findEntity({}) category not found", id);
                    return new ApiException(HttpStatus.NOT_FOUND, "Category not found");
                });
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getColor());
    }
}
