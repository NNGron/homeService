package ir.convert.category.category_response;

import ir.dto.category.CategoryResponseDto;
import ir.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Converter to transform a {@link Category} entity into a {@link CategoryResponseDto}.
 * This converter processes both single category entities and lists of categories into DTOs.
 */
@Slf4j
@Component
public class CategoryEntityToCategoryResponseDtoConvertor implements Function<Category, CategoryResponseDto> {

    @Override
    public CategoryResponseDto apply(Category category) {
        log.debug("Starting conversion of Category entity to CategoryResponseDto");

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setName(category.getName());
        categoryResponseDto.setBasePrice(category.getBasePrice());
        categoryResponseDto.setDescription(category.getDescription());

        if (category.getParentCategory() != null) {
            CategoryResponseDto parentResponseDto = new CategoryResponseDto();
            parentResponseDto.setName(category.getParentCategory() != null ? category.getParentCategory().getName() : "");
            categoryResponseDto.setParentCategory(parentResponseDto);
        }

        return categoryResponseDto;
    }

    public List<CategoryResponseDto> convertList(List<Category> categories) {
        log.debug("Starting batch conversion of Category entities to CategoryResponseDto");

        return categories.stream()
                .map(this)
                .toList();
    }
}
