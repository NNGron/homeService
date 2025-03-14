package ir.service.user;

import ir.convert.user.userResponse.UserEntityToUserResponseDtoConvertor;
import ir.dto.user.UserFilterDto;
import ir.dto.user.UserRequestDto;
import ir.dto.user.UserResponseDto;
import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.User;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import ir.entity.enums.UserType;
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

    public void saveUser(UserRequestDto userRequestDto) {
        UserType userType = userRequestDto.getUserType();
        if (userType != null && userType.equals(UserType.CUSTOMER)) {
            Customer customer = new Customer();
            customer.setFirstName(userRequestDto.getFirstName());
            customer.setLastName(userRequestDto.getLastName());
            customer.setUserName(userRequestDto.getUserName());
            customer.setPassword(userRequestDto.getPassword());
            customer.setEmail(userRequestDto.getEmail());
            customer.setAddress(userRequestDto.getAddress());
            customer.setCredit(userRequestDto.getCredit());
            customer.setStatus(UserStatus.NEW);
            customer.setRole(Role.USER);
            Customer savedCustomer = userRepository.save(customer);
        }
        if (userType != null && userType.equals(UserType.EXPERT)) {
            Expert expert = new Expert();
            expert.setFirstName(userRequestDto.getFirstName());
            expert.setLastName(userRequestDto.getLastName());
            expert.setUserName(userRequestDto.getUserName());
            expert.setPassword(userRequestDto.getPassword());
            expert.setEmail(userRequestDto.getEmail());
            expert.setAddress(userRequestDto.getAddress());
            expert.setCredit(userRequestDto.getCredit());
            expert.setPhoto(userRequestDto.getPhoto());
            expert.setSkills(userRequestDto.getSkills());
            expert.setStatus(UserStatus.NEW);
            expert.setRole(Role.USER);
            Expert savedExpert = userRepository.save(expert);
        }

    }
}
