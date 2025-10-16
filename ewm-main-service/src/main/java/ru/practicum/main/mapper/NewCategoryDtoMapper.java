package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.model.Category;


@UtilityClass
public class NewCategoryDtoMapper {
    public Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }
}


