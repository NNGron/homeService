package ir.controller;


import ir.dto.user.UserFilterDto;
import ir.dto.user.UserResponseDto;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<Page<UserResponseDto>> getAllCategories(@RequestBody UserFilterDto filterDto,
                                                                  @RequestParam(defaultValue = "0") int pageNumber,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "firstName") String sortBy,
                                                                  @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        Page<UserResponseDto> userResponseDtos = userService.getFilteredUsers(filterDto,pageable);
        return ResponseEntity.ok(userResponseDtos);
    }
}
