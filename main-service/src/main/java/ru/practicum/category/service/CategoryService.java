package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(NewCategoryDto dto, Long categoryId);

    void delete(Long categoryId);

    List<CategoryDto> find(Integer from, Integer size);

    CategoryDto findById(Long id);

}