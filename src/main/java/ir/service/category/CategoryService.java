package ir.service.category;

import ir.dto.category.CategoryResponseDto;
import ir.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryResponseDto> findAll();

    Optional<List<Category>> findAllByName(String name);
}
