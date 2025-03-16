package ir.controller;

import ir.dto.order.OrderResponseDto;
import ir.dto.user.*;
import ir.service.order.OrderService;
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
    private final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.saveUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all")
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
    public ResponseEntity<UserResponseDto> deleteExpertCategory(@RequestParam String email, @RequestParam List<String> categoryNames) {
        UserResponseDto userResponseDto = userService.deleteCategoryFromUser(email, categoryNames);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/expert-categories")
    public ResponseEntity<UserResponseDto> addExpertToCategories(@RequestParam String email, @RequestParam List<String> categoryNames) {
        UserResponseDto userResponseDto = userService.addExpertToCategories(email, categoryNames);
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping("/update-password")
    public ResponseEntity<UserMessageDto> updatePassword(@RequestBody UserUpdatePasswordDto updatePasswordDto) {
        UserMessageDto userMessageDto = userService.updateUser(updatePasswordDto);
        return ResponseEntity.ok(userMessageDto);
    }

    @PostMapping("/filter")
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
    public ResponseEntity<UserMessageDto> uploadImage(@RequestPart("image") MultipartFile image,
                                                       @RequestParam String email) {
        UserMessageDto userMessageDto = userService.uploadImage(email, image);// new
        return ResponseEntity.ok(userMessageDto);
    }


    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllUsers(@RequestParam String email,@RequestParam String password) {
        List<OrderResponseDto> orders = orderService.getOrder(email, password);
        return ResponseEntity.ok(orders);
    }

}
