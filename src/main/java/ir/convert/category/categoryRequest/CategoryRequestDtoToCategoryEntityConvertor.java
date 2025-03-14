package ir.convert.category.categoryRequest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.category.MainCategoryRequestDto;
import ir.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class CategoryRequestDtoToCategoryEntityConvertor implements Function<MainCategoryRequestDto, Category> {

    @Override
    public Category apply(MainCategoryRequestDto categoryRequestDto) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(categoryRequestDto, Category.class);
    }

    public List<Category> convertList(List<MainCategoryRequestDto> categoryRequestDtos) {
        return categoryRequestDtos.stream()
                .map(this)
                .toList();
    }
}
