package ru.practicum.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.common.GlobalConstant;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_FROM) Integer from,
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_SIZE) Integer size) {

        List<CategoryDto> categories = categoryService.getCategories(from, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        CategoryDto category = categoryService.getCategory(catId);
        return ResponseEntity.ok(category);
    }
}
