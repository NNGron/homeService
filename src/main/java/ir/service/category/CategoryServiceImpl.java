package ir.service.category;

import ir.convert.category.category_request.CategoryRequestDtoToCategoryEntityConvertor;
import ir.convert.category.category_response.CategoryEntityToCategoryResponseDtoConvertor;
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

/**
 * Service implementation for managing categories.
 *
 * <p>This class provides functionality to handle main categories and subcategories,
 * including creating, retrieving, and validating categories.</p>
 *
 * <p>Dependencies:</p>
 * <ul>
 *     <li>{@link CategoryRepository} - For database access.</li>
 *     <li>{@link CategoryEntityToCategoryResponseDtoConvertor} - For converting category entities to DTOs.</li>
 *     <li>{@link CategoryRequestDtoToCategoryEntityConvertor} - For converting request DTOs to category entities.</li>
 * </ul>
 */
@Slf4j
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryEntityToCategoryResponseDtoConvertor categoryEntityToCategoryResponseDtoConvertor;
    private final CategoryRequestDtoToCategoryEntityConvertor categoryRequestDtoToCategoryEntityConvertor;

    /**
     * Creates a new main category.
     *
     * <p>Validates the category request before saving and ensures there are no duplicates.</p>
     *
     * @param categoryRequestDto the details of the main category to be created
     * @return the saved main category as a {@link CategoryResponseDto}
     * @throws CustomException if validation fails or the category already exists
     */
    @Override
    public CategoryResponseDto save(@Valid MainCategoryRequestDto categoryRequestDto) {
        log.info("Validating main category creation for name: {}", categoryRequestDto.name());

        if (categoryRequestDto.name() == null || categoryRequestDto.name().isBlank()) {
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

    /**
     * Creates a new sub-category under an existing main category.
     *
     * @param subCategoryRequestDto the details of the sub-category to be created
     * @return the saved sub-category as a {@link CategoryResponseDto}
     * @throws CustomException if the sub-category already exists or the main category is not found
     */
    @Override
    public CategoryResponseDto saveSubCategory(@Valid SubCategoryRequestDto subCategoryRequestDto) {
        log.info("Validating sub-category creation for name: {}", subCategoryRequestDto.name());
        Optional<Category> optionalCategory = findSubCategoryByName(subCategoryRequestDto.name());
        if(optionalCategory.isPresent()) {
            log.error("Sub-category already exists: {}", subCategoryRequestDto.name());
            throw new CustomException("Sub category is already exist", ErrorCode.ALREADY_EXIST.getCode());
        }
        String parentName = subCategoryRequestDto.parentName();
        log.info("Searching for parent category: {}", parentName);
        Optional<List<Category>> byName = categoryRepository.findByName(parentName);

        if (byName.isPresent() && !byName.get().isEmpty()) {
            Category parentCategory = byName.get().getFirst();
            log.info("Parent category found: {}", parentCategory.getName());

            Category subCategory = new Category();
            subCategory.setName(subCategoryRequestDto.name());
            subCategory.setParentCategory(parentCategory);
            subCategory.setDescription(subCategoryRequestDto.description());
            subCategory.setBasePrice(subCategoryRequestDto.basePrice());
            Category savedSubCategory = categoryRepository.save(subCategory);
            return categoryEntityToCategoryResponseDtoConvertor.apply(savedSubCategory);
        } else {
            log.error("Main category not found: {}", parentName);
            throw new CustomException("Main category not found", ErrorCode.MAIN_CATEGORY_INVALID.getCode());
        }
    }

    /**
     * Retrieves all categories from the database.
     *
     * <p>This method fetches all categories, converts them to response DTOs,
     * and returns the resulting list.</p>
     *
     * @return a list of {@link CategoryResponseDto} representing all categories
     */
    @Override
    public List<CategoryResponseDto> findAll() {
        log.info("Fetching all categories from the database.");
        List<Category> categoryList = categoryRepository.findAll();
        log.info("Successfully retrieved {} categories from the database.", categoryList.size());
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    /**
     * Retrieves all parent categories from the database.
     *
     * <p>A parent category is defined as a category that does not have a parent category itself.
     * This method fetches all such categories and converts them into response DTOs.</p>
     *
     * @return a list of {@link CategoryResponseDto} representing all parent categories
     */
    @Override
    public List<CategoryResponseDto> findAllParentCategories() {
        log.info("Fetching all parent categories (categories with no parent category)");
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNull();
        log.info("Successfully retrieved {} parent categories from the database.", categoryList.size());
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    /**
     * Retrieves all subcategories from the database.
     *
     * <p>A subcategory is defined as a category that has a parent category.
     * This method fetches all such categories and converts them into response DTOs.</p>
     *
     * @return a list of {@link CategoryResponseDto} representing all subcategories
     */
    @Override
    public List<CategoryResponseDto> findAllSubCategories() {
        log.info("Fetching all subcategories (categories with a parent category).");
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNotNull();
        log.info("Successfully retrieved {} subcategories from the database.", categoryList.size());
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    /**
     * Retrieves subcategories based on the given list of names.
     *
     * <p>A subcategory is defined as a category that has a parent category. This
     * method finds all subcategories matching the provided names and returns them.</p>
     *
     * @param categoryNames the names of the subcategories to search for
     * @return a list of {@link Category} representing the found subcategories
     */
    @Override
    public List<Category> findSubCategoriesByNames(List<String> categoryNames) {
        log.info("Start searching for subcategories by names: {}", categoryNames);
        List<Category> subCategories =
                categoryRepository.findCategoriesByNameInAndParentCategoryIsNotNull(categoryNames);
        log.info("Found {} subcategories for names: {}", subCategories.size(), categoryNames);
        return subCategories;
    }

    /**
     * Retrieves all categories with pagination support.
     *
     * <p>This method fetches a paginated list of categories, converts them into response DTOs,
     * and returns the resulting list.</p>
     *
     * @param pageable pagination details specifying the page size and page number
     * @return a list of {@link CategoryResponseDto} representing the paginated categories
     */
    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        log.info("Fetching paginated categories with details: {}", pageable);
        List<Category> categoryList = categoryRepository.findAll(pageable).getContent();
        log.info("Successfully retrieved {} categories for the given pagination details.", categoryList.size());
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    /**
     * Finds a subcategory based on its name.
     *
     * <p>A subcategory is defined as a category with a parent category.
     * This method searches for a subcategory that matches the provided name.</p>
     *
     * @param name the name of the subcategory to search for
     * @return an {@link Optional} containing the matching subcategory, if found
     */
    @Override
    public Optional<Category> findSubCategoryByName(String name) {
        log.info("Searching for subcategory with name: {}", name);
         return categoryRepository.findCategoryByNameAndParentCategoryIsNotNull(name);
    }

    /**
     * Finds a main category based on its name.
     *
     * <p>A main category is defined as a category that does not have a parent category.
     * This method searches for a main category that matches the provided name.</p>
     *
     * @param name the name of the main category to search for
     * @return an {@link Optional} containing the matching main category, if found
     */
    @Override
    public Optional<Category> findMainCategoryByName(String name) {
        log.info("Searching for main category with name: {}", name);
        return categoryRepository.findCategoryByNameAndParentCategoryIsNull(name);
    }

    @Override
    public String deleteSubCategory(String subCategoryName) {
        Optional<Category> subCategory =
                categoryRepository.findCategoryByNameAndParentCategoryIsNotNull(subCategoryName);

        if (subCategory.isEmpty()) {
            log.warn("Sub-category '{}' not found in the database.", subCategoryName);
            throw new CustomException(
                    "Sub-category'" + subCategoryName + "' does not exist.", ErrorCode.NOT_FOUND.getCode());
        }
        Category category = subCategory.get();

        // بررسی ارتباطات با جدول Orders
        if (category.getOrders() != null && !category.getOrders().isEmpty()) {
            log.warn("Sub-category '{}' is still being used in Orders.", subCategoryName);
            throw new CustomException(
                    "Sub-category '" + subCategoryName + "' is associated with existing Orders and cannot be deleted.",
                    ErrorCode.CONFLICT.getCode());
        }

        // بررسی ارتباطات با جدول Experts
        if (category.getExperts() != null && !category.getExperts().isEmpty()) {
            log.warn("Sub-category '{}' is still being used by Experts.", subCategoryName);
            throw new CustomException(
                    "Sub-category '" + subCategoryName + "' is associated with existing Experts and cannot be deleted.",
                    ErrorCode.CONFLICT.getCode());
        }

        categoryRepository.delete(subCategory.get());
        log.info("Successfully deleted sub-category: {}", subCategoryName);
        return "Sub-category '" + subCategoryName + "' has been deleted successfully.";
    }


    @Override
    public String deleteMainCategory(String mainCategoryName) {
        Optional<Category> mainCategory =
                categoryRepository.findCategoryByNameAndParentCategoryIsNull(mainCategoryName);

        if (mainCategory.isPresent()) {
            Category category = mainCategory.get();

            List<Category> subCategories = categoryRepository.findCategoriesByParentCategoryIsNotNull()
                    .stream()
                    .filter(sub -> sub.getParentCategory() != null &&
                            sub.getParentCategory().getName().equals(mainCategoryName))
                    .toList();

            if (!subCategories.isEmpty()) {
                StringBuilder message = new StringBuilder(
                        "Main category '" + mainCategoryName + "' has the following sub-categories: ");
                subCategories.forEach(sub -> message.append(sub.getName()).append(", "));
                message.append("Please delete these sub-categories first.");
                throw new CustomException(message.toString(), ErrorCode.INVALID_DATA.getCode());
            }

            categoryRepository.delete(category);
            return "Main category '" + mainCategoryName + "' has been deleted successfully.";
        } else {
            throw new CustomException("Main category '" + mainCategoryName + "' does not exist.",
                    ErrorCode.NOT_FOUND.getCode());
        }
    }
}
