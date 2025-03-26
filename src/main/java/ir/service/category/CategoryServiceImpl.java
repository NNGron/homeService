package ir.service.category;

import ir.convert.category.categoryRequest.CategoryRequestDtoToCategoryEntityConvertor;
import ir.convert.category.categoryResponse.CategoryEntityToCategoryResponseDtoConvertor;
import ir.dto.category.CategoryResponseDto;
import ir.dto.category.MainCategoryRequestDto;
import ir.dto.category.SubCategoryRequestDto;
import ir.entity.Category;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;
    private final CategoryRequestDtoToCategoryEntityConvertor categoryRequestDtoToCategoryEntityConvertor;

    @Override
    public CategoryResponseDto save(@Valid MainCategoryRequestDto categoryRequestDto) {

        if (categoryRequestDto == null || categoryRequestDto.name() == null || categoryRequestDto.name().isBlank()) {
            log.error("Validation failed: Category name is missing or blank");
            throw new CustomException("Category name is required", ErrorCode.INVALID_DATA.getCode());
        }

        String name = categoryRequestDto.name();
        log.info("Checking if category already exists with name: {}", name);
        Optional<Category> optionalCategory = findMainCategoryByName(name);

        if (optionalCategory.isPresent()) {
            log.error("Category already exists: {}", name);
            throw new CustomException("Main category is already exist", ErrorCode.ALREADY_EXIST.getCode());
        }

        log.info("Creating new main category: {}", name);
        Category category = categoryRequestDtoToCategoryEntityConvertor.apply(categoryRequestDto);
        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDto categoryResponseDto = categoryEntityToCategoryResponseDtoConvertor.apply(savedCategory);

        log.info("Main category created successfully: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Override
    public CategoryResponseDto saveSubCategory(@Valid SubCategoryRequestDto subCategoryRequestDto) {
        log.info("Start saving sub-category: {}", subCategoryRequestDto.name());
        Optional<Category> optionalCategory = findSubCategoryByName(subCategoryRequestDto.name());
        if(optionalCategory.isPresent()) {
            throw new CustomException("Sub category is already exist", ErrorCode.ALREADY_EXIST.getCode());
        }
        String parentName = subCategoryRequestDto.parentName();
        Optional<List<Category>> byName = categoryRepository.findByName(parentName);
        if (byName.isPresent() && !byName.get().isEmpty()) {
            Category parentCategory = byName.get().getFirst();
            Category subCategory = new Category();
            subCategory.setName(subCategoryRequestDto.name());
            subCategory.setParentCategory(parentCategory);
            subCategory.setDescription(subCategoryRequestDto.description());
            subCategory.setBasePrice(subCategoryRequestDto.basePrice());
            Category savedSubCategory = categoryRepository.save(subCategory);
            CategoryResponseDto categoryResponseDto = categoryEntityToCategoryResponseDtoConvertor.apply(savedSubCategory);
            return categoryResponseDto;
        } else {
            log.error("Main category not found: {}", parentName);
            throw new CustomException("Main category not found", ErrorCode.MAIN_CATEGORY_INVALID.getCode());
        }
    }

    @Override
    public List<CategoryResponseDto> findAll() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<CategoryResponseDto> findAllParentCategories() {
        log.info("Fetching all parent categories (categories with no parent category)");
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNull();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<CategoryResponseDto> findAllSubCategories() {
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNotNull();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    /**
     * Finds subcategories based on the provided list of category names.
     *
     * <p>A subcategory is defined as a {@link Category} entity that has a non-null parent category.
     * This method retrieves all subcategories whose names match the given list of names.
     * Logging is used to trace the execution process and results.</p>
     *
     * @param categoryNames A list of category names to search for. Must not be null or empty.
     * @return A list of {@link Category} objects representing the subcategories found.
     * If no matching subcategories are found, an empty list is returned.
     * @see Category
     */
    @Override
    public List<Category> findSubCategoriesByNames(List<String> categoryNames) {
        log.info("Start searching for subcategories by names: {}", categoryNames);
        List<Category> subCategories =
                categoryRepository.findCategoriesByNameInAndParentCategoryIsNotNull(categoryNames);
        log.info("Found {} subcategories for names: {}", subCategories.size(), categoryNames);
        return subCategories;
    }

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        List<Category> categoryList = categoryRepository.findAll(pageable).getContent();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }


    @Override
    public Optional<Category> findSubCategoryByName(String name) {
         return categoryRepository.findCategoryByNameAndParentCategoryIsNotNull(name);
    }

    @Override
    public Optional<Category> findMainCategoryByName(String name) {
        return categoryRepository.findCategoryByNameAndParentCategoryIsNull(name);
    }

}
//    @Override
//    public Optional<List<Category>> findAllByName(String name) {
//        return categoryRepository.findByName(name);
//    }
//    @Override
//    public List<CategoryResponseDto> findAllSubCategoriesByNames(List<String> categoryNames) {
//        List<Category> categoryList = categoryRepository.findCategoriesByNameInAndParentCategoryIsNotNull(categoryNames);
//        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
//    }
