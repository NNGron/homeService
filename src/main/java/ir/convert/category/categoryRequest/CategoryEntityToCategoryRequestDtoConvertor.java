package ir.convert.category.categoryRequest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.category.CategoryRequestDto;
import ir.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CategoryEntityToCategoryRequestDtoConvertor implements Function<Category, CategoryRequestDto> {

    @Override
    public CategoryRequestDto apply(Category category) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(category, CategoryRequestDto.class);
    }

    public List<CategoryRequestDto> convertList(List<Category> categories) {
        return categories.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
