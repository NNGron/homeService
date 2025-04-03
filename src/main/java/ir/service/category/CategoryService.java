package ir.service.category;

import ir.dto.category.CategoryResponseDto;
import ir.dto.category.MainCategoryRequestDto;
import ir.dto.category.SubCategoryRequestDto;
import ir.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Defines operations for managing categories.
 *
 * <p>This service provides functionality for handling both main categories and subcategories,
 * including creating, retrieving, and searching for categories.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Retrieve all categories with optional pagination.</li>
 *     <li>Create new main categories and subcategories.</li>
 *     <li>Find categories by name or type (main/sub).</li>
 * </ul>
 */
public interface CategoryService {

    /**
     * Retrieves all categories.
     *
     * @return A list of {@link CategoryResponseDto} containing details of all categories.
     */
    List<CategoryResponseDto> findAll();

    /**
     * Retrieves all parent categories.
     *
     * <p>A parent category is defined as a category without a parent (top-level category).</p>
     *
     * @return A list of {@link CategoryResponseDto} containing details of all parent categories.
     */
    List<CategoryResponseDto> findAllParentCategories();

    /**
     * Retrieves all subcategories.
     *
     * <p>A subcategory is defined as a category with a valid parent category.</p>
     *
     * @return A list of {@link CategoryResponseDto} containing details of all subcategories.
     */
    List<CategoryResponseDto> findAllSubCategories();

    /**
     * Finds subcategories based on their names.
     *
     * <p>Subcategories are identified as categories with a non-null parent category.</p>
     *
     * @param categoryNames A list of names of subcategories to search for.
     * @return A list of {@link Category} entities matching the provided names.
     */
    List<Category> findSubCategoriesByNames(List<String> categoryNames);

    /**
     * Retrieves all categories with pagination.
     *
     * @param pageable A {@link Pageable} object specifying page number, size, and sorting criteria.
     * @return A paginated list of {@link CategoryResponseDto} containing details of categories.
     */
    List<CategoryResponseDto> findAll(Pageable pageable);

    /**
     * Saves a new main category.
     *
     * @param categoryRequestDto The details of the main category to be saved.
     * @return A {@link CategoryResponseDto} containing details of the saved main category.
     */
    CategoryResponseDto save(MainCategoryRequestDto categoryRequestDto);

    /**
     * Saves a new subcategory.
     *
     * @param subCategoryRequestDto The details of the subcategory to be saved.
     * @return A {@link CategoryResponseDto} containing details of the saved subcategory.
     */
    CategoryResponseDto saveSubCategory(SubCategoryRequestDto subCategoryRequestDto);

    /**
     * Finds a subcategory by its name.
     *
     * @param name The name of the subcategory to search for.
     * @return An {@link Optional} containing the {@link Category} if found, or empty if not.
     */
    Optional<Category> findSubCategoryByName(String name);

    /**
     * Finds a main category by its name.
     *
     * @param name The name of the main category to search for.
     * @return An {@link Optional} containing the {@link Category} if found, or empty if not.
     */
    Optional<Category> findMainCategoryByName(String name);

    String deleteSubCategory(String subCategoryName);

    String deleteMainCategory(String mainCategoryName);
}