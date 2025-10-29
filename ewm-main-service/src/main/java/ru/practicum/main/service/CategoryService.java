package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.mapper.CategoryDtoMapper;
import ru.practicum.main.mapper.NewCategoryDtoMapper;
import ru.practicum.main.storage.CategoryRepository;
import ru.practicum.main.storage.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    CategoryService(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Getting categories from: {}, size: {}", from, size);
        return categoryRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(CategoryDtoMapper::toCategoryDto)
                .collect(Collectors.toList());

    }

    public CategoryDto getCategory(Long catId) {
        log.info("Getting category by id: {}", catId);
        return CategoryDtoMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found")));
    }

    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Adding new category: {}", newCategoryDto.getName());
        return CategoryDtoMapper.toCategoryDto(categoryRepository.save(NewCategoryDtoMapper.toCategory(newCategoryDto)));
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Updating category with id: {}", catId);
        CategoryDto oldCategoryDto = getCategory(catId);
        oldCategoryDto.setName(categoryDto.getName());
        return CategoryDtoMapper.toCategoryDto(categoryRepository.save(CategoryDtoMapper.toCategory(oldCategoryDto)));
    }

    public void deleteCategory(Long catId) {
        log.info("Deleting category with id: {}", catId);
        getCategory(catId);
        categoryRepository.deleteById(catId);
    }
}
