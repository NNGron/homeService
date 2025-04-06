package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private final UserService userService;

    @PostMapping("/add-expert-subcategories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<String> addSubcategoriesToExpert(@RequestParam String expertEmail,
                                                           @Valid @RequestBody List<String> subCategoryNames) {
        log.info("Admin is attempting to add subcategories to expert with email: {}", expertEmail);

        userService.addExpertToCategoriesByAdmin(expertEmail, subCategoryNames);

        log.info("Successfully added subcategories to expert with email: {}", expertEmail);
        return ResponseEntity.ok("Subcategories added successfully to expert.");
    }
}
