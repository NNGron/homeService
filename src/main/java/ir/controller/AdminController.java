package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.service.category.CategoryService;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    /**
     * Adds subcategories to an expert's profile.
     *
     * @param adminEmail       the admin's email for authentication
     * @param adminPassword    the admin's password for authentication
     * @param expertEmail      the email of the expert to whom the subcategories will be added
     * @param subCategoryNames the list of subcategories to add
     * @return a message confirming the operation or an error if it fails
     */
    @PostMapping("/add-expert-subcategories")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<String> addSubcategoriesToExpert(@RequestParam String adminEmail,
                                                           @RequestParam String adminPassword,
                                                           @RequestParam String expertEmail,
                                                           @RequestBody List<String> subCategoryNames) {

        log.info("Admin with email {} is attempting to add subcategories to expert with email {}", adminEmail, expertEmail);
        userService.authenticateAdmin(adminEmail, adminPassword);
        userService.addExpertToCategoriesByAdmin(expertEmail, subCategoryNames);

        log.info("Successfully added subcategories {} to expert with email {}", subCategoryNames, expertEmail);
        return ResponseEntity.ok("Subcategories added successfully to expert.");
    }
}
