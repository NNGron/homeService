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
 * Converter to transform a {@link MainCategoryRequestDto} into a {@link Category} entity.
 * This converter processes both single request DTOs and lists of request DTOs into entities.
 */
@Slf4j
@Component
public class CategoryRequestDtoToCategoryEntityConvertor implements Function<MainCategoryRequestDto, Category> {

    @Override
    public Category apply(MainCategoryRequestDto categoryRequestDto) {
        log.debug("Starting conversion of MainCategoryRequestDto to Category entity");

        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(categoryRequestDto, Category.class);
    }

    public List<Category> convertList(List<MainCategoryRequestDto> categoryRequestDtos) {
        log.debug("Starting batch conversion of MainCategoryRequestDtos to Category entities");

        return categoryRequestDtos.stream()
                .map(this)
                .toList();
    }
}
