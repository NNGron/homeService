package ir.service.user;

import ir.convert.user.userResponse.UserEntityToUserResponseDtoConvertor;
import ir.dto.user.UserFilterDto;
import ir.dto.user.UserResponseDto;
import ir.entity.User;
import ir.repository.UserRepository;
import ir.repository.UserSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;


    public Page<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(filterDto);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<User> userList = userPage.getContent();

        // Convert List<User> to List<UserResponseDto>
        List<UserResponseDto> userResponseDtos = userEntityToUserResponseDtoConvertor.convertList(userList);

        // Return as Page<UserResponseDto>
        return new PageImpl<>(userResponseDtos, pageable, userPage.getTotalElements());
    }
}
