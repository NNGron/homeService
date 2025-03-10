package ir.convert.category.categoryResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(category, CategoryResponseDto.class);
    }

    public List<CategoryResponseDto> convertList(List<Category> categories) {
        return categories.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
