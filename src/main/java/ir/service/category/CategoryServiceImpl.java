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
    public List<CategoryResponseDto> findAll() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<CategoryResponseDto> findAllParentCategories() {
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNull();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<CategoryResponseDto> findAllSubCategories() {
        List<Category> categoryList = categoryRepository.findCategoriesByParentCategoryIsNotNull();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<CategoryResponseDto> findAllSubCategoriesByNames(List<String> categoryNames) {
        List<Category> categoryList = categoryRepository.findCategoriesByNameInAndParentCategoryIsNotNull(categoryNames);
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public List<Category> findSubCategoriesByNames(List<String> categoryNames) {
        return categoryRepository.findCategoriesByNameInAndParentCategoryIsNotNull(categoryNames);
    }

    @Override
    public Optional<List<Category>> findAllByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<CategoryResponseDto> findAll(Pageable pageable) {
        List<Category> categoryList = categoryRepository.findAll(pageable).getContent();
        return categoryEntityToCategoryResponseDtoConvertor.convertList(categoryList);
    }

    @Override
    public CategoryResponseDto save(MainCategoryRequestDto categoryRequestDto) {
        String name = categoryRequestDto.name();
        Optional<Category> optionalCategory = findMainCategoryByName(name);
        if (optionalCategory.isPresent()) {
            throw new CustomException("Main category is already exist", ErrorCode.ALREADY_EXIST.getCode());
        }
        Category category = categoryRequestDtoToCategoryEntityConvertor.apply(categoryRequestDto);
        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDto categoryResponseDto = categoryEntityToCategoryResponseDtoConvertor.apply(savedCategory);
        return categoryResponseDto;
    }

    @Override
    public CategoryResponseDto saveSubCategory(SubCategoryRequestDto subCategoryRequestDto) {
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
    public Optional<Category> findSubCategoryByName(String name) {
         return categoryRepository.findCategoryByNameAndParentCategoryIsNotNull(name);
    }

    @Override
    public Optional<Category> findMainCategoryByName(String name) {
        return categoryRepository.findCategoryByNameAndParentCategoryIsNull(name);
    }

}
