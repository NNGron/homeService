package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.user.*;
import ir.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.saveUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all-user")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "firstName") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<UserResponseDto> aLldUsers = userService.getALldUsers(pageable);
        return ResponseEntity.ok(aLldUsers);
    }

    @DeleteMapping("/expert-category")
    @Operation(summary = "(Admin & Expert)")
    public ResponseEntity<UserResponseDto> deleteExpertCategory(@RequestParam String email,
                                                                @RequestParam List<String> categoryNames) {
        UserResponseDto userResponseDto = userService.deleteCategoryFromUser(email, categoryNames);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/add-expert-categories")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<UserResponseDto> addExpertToCategories(@RequestParam String email,
                                                                 @RequestParam List<String> categoryNames) {
        UserResponseDto userResponseDto = userService.addExpertToCategories(email, categoryNames);
        return ResponseEntity.ok(userResponseDto);
    }


    @PutMapping("/update-password")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserMessageDto> updatePassword(@RequestParam String email,
                                                         @RequestParam String oldPassword,
                                                         @RequestParam String newPassword) {
        UserMessageDto userMessageDto = userService.updateUserPassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(userMessageDto);
    }


    @PutMapping("/approve-user")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserMessageDto> updateUserStatus(@RequestParam String email) {
        UserMessageDto userMessageDto = userService.updateStatusUser(email);
        return ResponseEntity.ok(userMessageDto);
    }


    @PostMapping("/filter-users")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<UserResponseDto>> getAllCategories(@Valid @RequestBody UserFilterDto filterDto,
                                                                  @RequestParam(defaultValue = "0") int pageNumber,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "firstName") String sortBy,
                                                                  @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<UserResponseDto> userResponseDtos = userService.getFilteredUsers(filterDto, pageable);
        return ResponseEntity.ok(userResponseDtos);
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "(Expert only)")
    public ResponseEntity<UserMessageDto> uploadImage(@RequestPart("image") MultipartFile image,
                                                       @RequestParam String email) {
        UserMessageDto userMessageDto = userService.uploadImage(email, image);// new
        return ResponseEntity.ok(userMessageDto);
    }

    @DeleteMapping("/delete-user")
    @Operation(summary = "(Customer or Expert)")
    public ResponseEntity<String> deleteUser(@RequestParam String email,
                                             @RequestParam String password) {
        userService.deleteUser(email, password);
        return ResponseEntity.ok("User with email " + email + " has been successfully deleted.");
    }

    @PutMapping("/update_users")
    @Operation(summary = "(Customer & Expert)")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto,
                                                    @RequestParam String email,
                                                    @RequestParam String password) {
        UserResponseDto responseDto = userService.updateUser(userUpdateRequestDto, email, password);
        return ResponseEntity.ok(responseDto);
    }
}

