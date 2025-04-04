package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.user.*;
import ir.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto requestDto) {
        log.info("Received request to sign up a new user: {}", requestDto.getEmail());
        log.debug("User details for signup: {}", requestDto);

        UserResponseDto responseDto = userService.saveUser(requestDto);

        log.info("Successfully signed up user: {}", requestDto.getEmail());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<UserMessageDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Starting login process for user: {}", loginRequestDto.getEmail());
        log.debug("Login request details: {}", loginRequestDto);

        boolean isAuthenticated =
                userService.authenticateUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        if (isAuthenticated) {
            log.info("User successfully logged in: {}", loginRequestDto.getEmail());
            return ResponseEntity.ok(new UserMessageDto("Login Successful"));
        } else {
            log.warn("Login failed for user: {}", loginRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserMessageDto("Invalid credentials"));
        }
    }


    @GetMapping("/all-user")
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
    public ResponseEntity<UserResponseDto> deleteExpertCategory(@RequestParam String email,
                                                                @RequestParam List<String> categoryNames) {
        log.info("Deleting categories from expert with email: {}", email);
        log.debug("Categories to delete: {}", categoryNames);

        UserResponseDto userResponseDto = userService.deleteCategoryFromUser(email, categoryNames);

        log.info("Successfully deleted categories for expert with email: {}", email);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/add-expert-categories")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<UserResponseDto> addExpertToCategories(@RequestParam String email,
                                                                 @RequestParam List<String> categoryNames) {
        log.info("Adding categories to expert with email: {}", email);
        log.debug("Categories to add: {}", categoryNames);

        UserResponseDto userResponseDto = userService.addExpertToCategories(email, categoryNames);

        log.info("Successfully added categories for expert with email: {}", email);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/update-password")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserMessageDto> updatePassword(@RequestParam String email,
                                                         @RequestParam String oldPassword,
                                                         @RequestParam String newPassword) {
        log.info("Updating password for user with email: {}", email);

        UserMessageDto userMessageDto = userService.updateUserPassword(email, oldPassword, newPassword);

        log.info("Successfully updated password for user with email: {}", email);
        return ResponseEntity.ok(userMessageDto);
    }

    @PutMapping("/approve-user")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserMessageDto> updateUserStatus(@RequestParam String email) {
        log.info("Approving user with email: {}", email);

        UserMessageDto userMessageDto = userService.updateStatusUser(email);

        log.info("Successfully approved user with email: {}", email);
        return ResponseEntity.ok(userMessageDto);
    }

    @PostMapping("/filter-users")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<UserResponseDto>> getAllCategories(@Valid @RequestBody UserFilterDto filterDto,
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
                                                       @RequestParam String email) {
        log.info("Uploading image for user with email: {}", email);

        UserMessageDto userMessageDto = userService.uploadImage(email, image);// new

        log.info("Successfully uploaded image for user with email: {}", email);
        return ResponseEntity.ok(userMessageDto);
    }

    @DeleteMapping("/delete-user")
    @Operation(summary = "(Customer or Expert)")
    public ResponseEntity<String> deleteUser(@RequestParam String email,
                                             @RequestParam String password) {
        log.info("Deleting user with email: {}", email);

        userService.deleteUser(email, password);

        log.info("Successfully deleted user with email: {}", email);
        return ResponseEntity.ok("User with email " + email + " has been successfully deleted.");
    }

    @PutMapping("/update_users")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto,
                                                    @RequestParam String email,
                                                    @RequestParam String password) {
        log.info("Updating user with email: {}", email);
        log.debug("Updated user details: {}", userUpdateRequestDto);

        UserResponseDto responseDto = userService.updateUser(userUpdateRequestDto, email, password);

        log.info("Successfully updated user with email: {}", email);
        return ResponseEntity.ok(responseDto);
    }
}