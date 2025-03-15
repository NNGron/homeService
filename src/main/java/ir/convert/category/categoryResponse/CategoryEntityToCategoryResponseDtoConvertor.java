package ir.convert.category.categoryResponse;

import ir.dto.category.CategoryResponseDto;
import ir.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CategoryEntityToCategoryResponseDtoConvertor implements Function<Category, CategoryResponseDto> {

    @Override
    public CategoryResponseDto apply(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setName(category.getName());
        categoryResponseDto.setBasePrice(category.getBasePrice());
        categoryResponseDto.setDescription(category.getDescription());

        if ( category.getParentCategory() != null ) {
            CategoryResponseDto parentResponseDto = new CategoryResponseDto();
            parentResponseDto.setName(category.getParentCategory() != null ? category.getParentCategory().getName() : "");
            categoryResponseDto.setParentCategory(parentResponseDto);
        }

        return categoryResponseDto;
    }

    public List<CategoryResponseDto> convertList(List<Category> categories) {
        return categories.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
