package ir.convert.category.category_request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.category.MainCategoryRequestDto;
import ir.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Converter to transform a {@link Category} entity into a {@link MainCategoryRequestDto}.
 * This converter processes single category entities and lists of categories.
 */
@Slf4j
@Component
public class CategoryEntityToCategoryRequestDtoConvertor implements Function<Category, MainCategoryRequestDto> {

    @Override
    public MainCategoryRequestDto apply(Category category) {
        log.debug("Starting conversion of Category entity to MainCategoryRequestDto");

        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(category, MainCategoryRequestDto.class);
    }

    public List<MainCategoryRequestDto> convertList(List<Category> categories) {
        log.debug("Starting batch conversion of Category entities to MainCategoryRequestDto");

        return categories.stream()
                .map(this)
                .toList();
    }
}
