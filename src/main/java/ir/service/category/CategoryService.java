package ir.service.category;

import ir.dto.category.MainCategoryRequestDto;
import ir.dto.category.CategoryResponseDto;
import ir.dto.category.SubCategoryRequestDto;
import ir.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryResponseDto> findAll();

    List<CategoryResponseDto> findAllParentCategories();

    List<CategoryResponseDto> findAllSubCategories();

    Optional<List<Category>> findAllByName(String name);

    List<CategoryResponseDto> findAll(Pageable pageable);

    CategoryResponseDto save(MainCategoryRequestDto categoryRequestDto);

    CategoryResponseDto saveSubCategory(SubCategoryRequestDto subCategoryRequestDto);
}
