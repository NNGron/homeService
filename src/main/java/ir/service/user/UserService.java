package ir.service.user;

import ir.convert.order.OrderResponse.OrderEntityToOrderResponseDtoConvertor;
import ir.convert.user.userResponse.UserEntityToUserResponseDtoConvertor;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.dto.user.*;
import ir.entity.*;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import ir.entity.enums.UserType;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.user.UserRepository;
import ir.repository.user.UserSpecification;
import ir.service.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    public List<UserResponseDto> getALldUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> userList = userPage.getContent();
        return userEntityToUserResponseDtoConvertor.convertList(userList);
    }

    public UserResponseDto deleteCategoryFromUser(String email, List<String> categoryNames) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        List<Category> shouldBeRemoveCategoryList = categoryService.findSubCategoriesByNames(categoryNames);
        Expert expert = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)){
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user instanceof Expert) {
                expert = (Expert) user;
                if (user.getStatus().equals(UserStatus.NEW)){
                    throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
                }
                List<Category> categories = expert.getCategories();
                categories.removeAll(shouldBeRemoveCategoryList);
                expert.setCategories(categories);
                expert = userRepository.save(expert);
            }
        }
        UserResponseDto responseDto = userEntityToUserResponseDtoConvertor.apply(expert);
        return responseDto;
    }

    public UserResponseDto addExpertToCategories(String email, List<String> categoryNames) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        List<Category> shouldBeAdd = categoryService.findSubCategoriesByNames(categoryNames);
        Expert expert = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)){
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user instanceof Expert) {
                expert = (Expert) user;
                if (user.getStatus().equals(UserStatus.NEW)){
                    throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
                }
                List<Category> categories = expert.getCategories();
                categories.addAll(shouldBeAdd);
                expert.setCategories(categories);
                expert = userRepository.save(expert);
            }
        }
        UserResponseDto responseDto = userEntityToUserResponseDtoConvertor.apply(expert);
        return responseDto;
    }

    public List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable) {
        List<String> categoryNames = filterDto.categoryNames();
        List<Category> categoryList = categoryNames != null ? categoryService.findSubCategoriesByNames(categoryNames) : List.of();

        Specification<User> spec = UserSpecification.filterUsers(filterDto, categoryList);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<User> userList = userPage.getContent();
        return userEntityToUserResponseDtoConvertor.convertList(userList);
    }

    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        log.info("Start saving user: {}", userRequestDto);
        User user = null;
        UserType userType = userRequestDto.getUserType();
        if (userType != null && userType.equals(UserType.CUSTOMER)) {
            Customer customer = new Customer();
            customer.setFirstName(userRequestDto.getFirstName());
            customer.setLastName(userRequestDto.getLastName());
            customer.setUserName(userRequestDto.getUserName());
            customer.setPassword(userRequestDto.getPassword());
            customer.setEmail(userRequestDto.getEmail());
            customer.setAddress(userRequestDto.getAddress());
            customer.setStatus(UserStatus.NEW);
            customer.setRole(Role.USER);
            Customer savedCustomer = userRepository.save(customer);
            user = savedCustomer;
        }
        if (userType != null && userType.equals(UserType.EXPERT)) {
            Expert expert = new Expert();
            expert.setFirstName(userRequestDto.getFirstName());
            expert.setLastName(userRequestDto.getLastName());
            expert.setUserName(userRequestDto.getUserName());
            expert.setPassword(userRequestDto.getPassword());
            expert.setEmail(userRequestDto.getEmail());
            expert.setAddress(userRequestDto.getAddress());

            List<String> subCategoryNames = userRequestDto.getSubCategoryNames();
            List<Category> categoryList = categoryService.findSubCategoriesByNames(subCategoryNames);
            if (categoryList == null || categoryList.isEmpty()) {
                log.error("Could not find categories by given names.");
                throw new CustomException("Category is invalid", ErrorCode.CATEGORY_INVALID.getCode());
            }
            expert.setCategories(categoryList);

            expert.setStatus(UserStatus.NEW);
            expert.setRole(Role.USER);
            Expert savedExpert = userRepository.save(expert);
            user = savedExpert;
        }
        UserResponseDto responseDto = userEntityToUserResponseDtoConvertor.apply(user);
        return responseDto;
    }

//    public UserMessageDto updateUser(UserUpdatePasswordDto userUpdatePasswordDto) {
//        String password = userUpdatePasswordDto.newPassword();
//        String email = userUpdatePasswordDto.email();
//        Optional<User> byEmail = userRepository.findByEmail(email);
//        if (byEmail.isPresent()) {
//            User user = byEmail.get();
//            if (user.getStatus().equals(UserStatus.NEW)){
//                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
//            }
//            user.setPassword(password);
//            userRepository.save(user);
//        } else {
//            log.error("User with email {} not found", email);
//            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
//        }
//        return new UserMessageDto("Password updated successfully");
//    }

    public UserMessageDto updateUserPassword (String email, String oldPassword,String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, oldPassword);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)){
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            user.setPassword(newPassword);
            userRepository.save(user);
        } else {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }
        return new UserMessageDto("Password updated successfully");
    }

    public UserMessageDto updateStatusUser(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (user.getStatus().equals(UserStatus.NEW)){
                user.setStatus(UserStatus.APPROVED);
            }
            userRepository.save(user);
        } else {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }
        return new UserMessageDto("Status updated is approved");
    }

    public UserMessageDto uploadImage(String email, MultipartFile image) {
        if (image.getSize() > 300 * 1024){
            throw new CustomException("Image size must to be less than 300KB", ErrorCode.PHOTO_INVALID.getCode());
        }
        userRepository.findByEmail(email).ifPresent(user -> {
            if(user instanceof Expert){
                if (user.getStatus().equals(UserStatus.NEW)){
                    throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
                }
                Expert expert = (Expert) user;
                try {
                    expert.setPhoto(image.getBytes());
                    userRepository.save(expert);
                } catch (IOException e) {
                    throw new CustomException("Can not upload image", ErrorCode.IMAGE_CAN_NOT_UPLOAD.getCode());
                }
            }
        });
        return new UserMessageDto("Image uploaded successfully");
    }

    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode()));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with provided email", ErrorCode.NOT_FOUND.getCode()));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserResponseDto addCreditToCustomer(String email, double amount) {
        User user = findByEmail(email);
        Double credit = user.getCredit() != null ? user.getCredit() : 0;

        if ((credit + amount < 0)) {
            throw new CustomException("Amount is invalid", ErrorCode.INVALID_AMOUNT.getCode());
        }

        credit += amount;
        user.setCredit(credit);
        user = saveUser(user);
        return userEntityToUserResponseDtoConvertor.apply(user);
    }
}
