package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.category.CategoryResponseDto;
import ir.dto.category.MainCategoryRequestDto;
import ir.dto.category.SubCategoryRequestDto;
import ir.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create-main-category")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<CategoryResponseDto> saveCategory(
            @Valid @RequestBody MainCategoryRequestDto mainCategoryRequestDto) {
        CategoryResponseDto responseDto = categoryService.save(mainCategoryRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/create-sub-category")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<CategoryResponseDto> saveSubCategory(
            @Valid @RequestBody SubCategoryRequestDto subCategoryRequestDto) {
        CategoryResponseDto responseDto = categoryService.saveSubCategory(subCategoryRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/main-categories")
    public ResponseEntity<List<CategoryResponseDto>> getParentCategories() {
        List<CategoryResponseDto> categories = categoryService.findAllParentCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/sub-categories")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories() {
        List<CategoryResponseDto> categories = categoryService.findAllSubCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/pageable")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<CategoryResponseDto> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/pageable-sort")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "name") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<CategoryResponseDto> categories = categoryService.findAll(pageable);
        return ResponseEntity.ok(categories);
    }
}
