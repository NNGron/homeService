package ir.convert.category.categoryRequest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.category.MainCategoryRequestDto;
import ir.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CategoryEntityToCategoryRequestDtoConvertor implements Function<Category, MainCategoryRequestDto> {

    @Override
    public MainCategoryRequestDto apply(Category category) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(category, MainCategoryRequestDto.class);
    }

    public List<MainCategoryRequestDto> convertList(List<Category> categories) {
        return categories.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
