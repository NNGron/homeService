package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.category.CategoryResponseDto;
import ir.dto.category.MainCategoryRequestDto;
import ir.dto.category.SubCategoryRequestDto;
import ir.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create-main-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<CategoryResponseDto> saveCategory(
            @Valid @RequestBody MainCategoryRequestDto mainCategoryRequestDto) {

        log.debug("Request details: {}", mainCategoryRequestDto);
        CategoryResponseDto responseDto = categoryService.save(mainCategoryRequestDto);

        log.info("Successfully created main category with name: {}", responseDto.getName());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create-sub-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<CategoryResponseDto> saveSubCategory(
            @Valid @RequestBody SubCategoryRequestDto subCategoryRequestDto) {

        log.debug("Request details: {}", subCategoryRequestDto);
        CategoryResponseDto responseDto = categoryService.saveSubCategory(subCategoryRequestDto);

        log.info("Successfully created subcategory with name: {}", responseDto.getName());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/main-categories")
    public ResponseEntity<List<CategoryResponseDto>> getParentCategories() {
        log.info("Fetching all main categories...");

        List<CategoryResponseDto> categories = categoryService.findAllParentCategories();

        log.info("Successfully fetched {} main categories.", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/sub-categories")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories() {
        log.info("Fetching all subcategories...");

        List<CategoryResponseDto> categories = categoryService.findAllSubCategories();

        log.info("Successfully fetched {} subcategories.", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/pageable")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching categories with pagination - pageNumber: {}, pageSize: {}", pageNumber, size);

        Pageable pageable = PageRequest.of(pageNumber, size);
        List<CategoryResponseDto> categories = categoryService.findAll(pageable);

        log.info("Successfully fetched {} categories with pagination.", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/pageable-sort")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "name") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching categories with pagination and sorting - pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}",
                pageNumber, size, sortBy, sortDir);

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<CategoryResponseDto> categories = categoryService.findAll(pageable);

        log.info("Successfully fetched {} categories with pagination and sorting.", categories.size());
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/delete-sub-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<String> deleteSubCategory(@RequestParam String subCategoryName) {
        log.info("Received request to delete subcategory with name: {}", subCategoryName);

        String message = categoryService.deleteSubCategory(subCategoryName);

        log.info("Successfully deleted subcategory with name: {}", subCategoryName);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete-main-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<String> deleteMainCategory(@RequestParam String mainCategoryName) {
        log.info("Received request to delete main category with name: {}", mainCategoryName);

        String message = categoryService.deleteMainCategory(mainCategoryName);

        log.info("Successfully deleted main category with name: {}", mainCategoryName);
        return ResponseEntity.ok(message);
    }
}