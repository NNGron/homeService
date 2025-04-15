package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.bankcard.BankCardRequestDto;
import ir.dto.bankcard.BankCardResponseDto;
import ir.dto.user.*;
import ir.service.card.CardService;
import ir.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;
    private final CardService cardService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto requestDto) {
        log.info("Received request to sign up a new user: {}", requestDto.getEmail());
        log.debug("User details for signup: {}", requestDto);

        UserResponseDto responseDto = userService.saveUser(requestDto);

        log.info("Successfully signed up user: {}", requestDto.getEmail());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Starting login process for user: {}", loginRequestDto.getEmail());
        log.debug("Login request details: {}", loginRequestDto);

        LoginResponseDto loginResponseDto =
                userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/all-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "firstName") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Fetching all users with pagination - pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}",
                pageNumber, size, sortBy, sortDir);

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<UserResponseDto> allUsers = userService.getAllUsers(pageable);

        log.info("Successfully fetched {} users.", allUsers.size());
        return ResponseEntity.ok(allUsers);
    }

    @DeleteMapping("/expert-category")
    @Operation(summary = "(Admin & Expert)")
    public ResponseEntity<UserResponseDto> deleteExpertCategory(@RequestHeader("Authorization") String token,
                                                                @RequestParam String email,
                                                                @RequestParam List<String> categoryNames) {
        log.info("Request to delete categories from expert with email: {}", email);

        UserResponseDto userResponseDto = userService.deleteExpertCategory(token, email, categoryNames);

        log.info("Successfully deleted categories for expert with email: {}", email);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/add-expert-categories")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<UserResponseDto> addExpertToCategories(@RequestHeader("Authorization") String token,
                                                                 @RequestParam List<String> categoryNames) {
        log.info("Request to add categories to logged-in expert with token.");
        log.debug("Categories to add: {}", categoryNames);

        UserResponseDto userResponseDto = userService.addExpertToCategories(token, categoryNames);

        log.info("Successfully added categories for the expert.");
        return ResponseEntity.ok(userResponseDto);
    }


    @PutMapping("/update-password")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserMessageDto> updatePassword(@RequestHeader("Authorization") String token,
                                                         @RequestParam String newPassword) {

        UserMessageDto userMessageDto = userService.updateUserPassword(token, newPassword);
        return ResponseEntity.ok(userMessageDto);
    }

    @PutMapping("/approve-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserMessageDto> updateUserStatus(@RequestParam String email) {
        log.info("Approving user with email: {}", email);

        UserMessageDto userMessageDto = userService.updateStatusUser(email);

        log.info("Successfully approved user with email: {}", email);
        return ResponseEntity.ok(userMessageDto);
    }

    @PostMapping("/filter-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<UserResponseDto>> filterUsers(@Valid @RequestBody UserFilterDto filterDto,
                                                             @RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "firstName") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Filtering users with criteria: {}", filterDto);

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<UserResponseDto> userResponseDtos = userService.getFilteredUsers(filterDto, pageable);

        log.info("Successfully filtered {} users.", userResponseDtos.size());
        return ResponseEntity.ok(userResponseDtos);
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "(Expert only)")
    public ResponseEntity<UserMessageDto> uploadImage(@RequestPart("image") MultipartFile image,
                                                      @RequestHeader("Authorization") String token) {
        log.info("Uploading image for user with token");

        UserMessageDto userMessageDto = userService.uploadImage(token, image);// new

        log.info("Successfully uploaded image for user with token");
        return ResponseEntity.ok(userMessageDto);
    }

    @DeleteMapping("/delete-user")
    @Operation(summary = "(Customer or Expert)")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        log.info("Deleting user with token");

        userService.deleteUser(token);

        log.info("Successfully deleted user with token");
        return ResponseEntity.ok("User has been successfully deleted.");
    }

    @PutMapping("/update_users")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserResponseDto> saveUser(@RequestHeader("Authorization") String token,
                                                    @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        log.info("Attempting to update user with token.");
        log.debug("Updated user details: {}", userUpdateRequestDto);

        UserResponseDto responseDto = userService.updateUser(userUpdateRequestDto, token);

        log.info("Successfully updated user details.");
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/add/bank-card")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<BankCardResponseDto> saveCardBank(@RequestHeader("Authorization") String token,
                                                            @Valid @RequestBody BankCardRequestDto bankCardRequestDto) {
        BankCardResponseDto bankCardResponseDto = cardService.add(token, bankCardRequestDto);

        return ResponseEntity.ok(bankCardResponseDto);
    }

    @GetMapping("/registrations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<Map<String, Long>> getRegistrationReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Long> report = userService.getRegistrationReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/users-summary")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<Map<String, Object>> getExpertAndCustomerReport(
            @RequestParam("registrationStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registrationStartDate,
            @RequestParam("registrationEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registrationEndDate) {

        Map<String, Object> report = userService.getExpertAndCustomerReport(registrationStartDate, registrationEndDate);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/users-summary-by-email")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get user reports by email and registration date (Admin only)")
    public ResponseEntity<Map<String, Object>> getExpertAndCustomerReportByEmail(
            @RequestParam("email") String email, // ایمیل برای فیلتر کاربر
            @RequestParam("registrationStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registrationStartDate,
            @RequestParam("registrationEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registrationEndDate) {

        // فراخوانی سرویس با ایمیل و بازه زمانی
        Map<String, Object> report = userService.getExpertAndCustomerReportByEmail(email, registrationStartDate, registrationEndDate);

        return ResponseEntity.ok(report);
    }

}