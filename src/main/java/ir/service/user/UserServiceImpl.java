package ir.service.user;

import ir.convert.order.OrderResponse.OrderEntityToOrderResponseDtoConvertor;
import ir.convert.user.UserEntityToUserResponseDtoConvertor;
import ir.dto.credit.CreditResponseDto;
import ir.dto.user.UserFilterDto;
import ir.dto.user.UserMessageDto;
import ir.dto.user.UserRequestDto;
import ir.dto.user.UserResponseDto;
import ir.entity.Category;
import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.User;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    /**
     * Registers and saves a new user in the system based on the provided user details.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *   <li>Validates the mandatory fields (email and password).</li>
     *   <li>Validates the password format to ensure it meets security requirements
     *       (must contain at least one letter and one number).</li>
     *   <li>Checks if a user with the same email and password already exists in the system.</li>
     *   <li>Creates a new {@link Customer} or {@link Expert} entity based on the user type:</li>
     *   <ul>
     *      <li>If the user is a Customer, assigns default roles and status, and saves it as a Customer entity.</li>
     *      <li>If the user is an Expert, fetches and validates the provided sub-categories,
     *          assigns them to the Expert, and saves it as an Expert entity.</li>
     *   </ul>
     *   <li>Converts the saved user entity to a {@link UserResponseDto} for returning the response.</li>
     * </ol>
     *
     * <p>Extensive logging is implemented at every stage for traceability and debugging.
     * Appropriate exceptions are thrown if validation fails or user creation encounters issues.</p>
     *
     * @param userRequestDto The user details provided for registration.
     *                       Must include a valid email, password, user type, and additional details
     *                       depending on the user type.
     * @return A {@link UserResponseDto} containing the details of the newly created user.
     * @throws CustomException If:
     *                         <ul>
     *                           <li>Mandatory fields (email or password) are missing or empty.</li>
     *                           <li>Password does not meet the format requirements.</li>
     *                           <li>A user with the same email and password already exists.</li>
     *                           <li>The provided subcategories for an Expert are invalid or not found.</li>
     *                         </ul>
     * @see UserRequestDto
     * @see UserResponseDto
     * @see UserType
     * @see Customer
     * @see Expert
     * @see Category
     */
    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        log.info("Starting user registration: {}", userRequestDto);

        // Validate required fields
        if (StringUtils.isEmpty(userRequestDto.getEmail()) || StringUtils.isEmpty(userRequestDto.getPassword())) {
            log.error("Validation failed: Email or Password is empty. UserRequestDto: {}", userRequestDto);
            throw new CustomException("Email and Password must not be empty", ErrorCode.INVALID_EMAIL_OR_PASSWORD.getCode());
        }

        // Validate password
        if (!userRequestDto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            log.error("Validation failed: Password must contain at least one letter and one number. Email: {}", userRequestDto.getEmail());
            throw new CustomException("Password must contain at least one letter and one number", ErrorCode.INVALID_EMAIL_OR_PASSWORD.getCode());
        }

        // Check if the user already exists
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(
                userRequestDto.getEmail(), userRequestDto.getPassword());
        if (optionalUser.isPresent()) {
            log.error("Email already exists: {}", userRequestDto.getEmail());
            throw new CustomException("Email already exist", ErrorCode.ALREADY_EXIST.getCode());
        }

        log.info("Creating a new user with type: {}", userRequestDto.getUserType());
        User user = null;
        UserType userType = userRequestDto.getUserType();

        // If the user type is CUSTOMER
        if (userType != null && userType.equals(UserType.CUSTOMER)) {
            log.info("User type CUSTOMER detected. Creating a Customer entity.");
            Customer customer = new Customer();
            customer.setFirstName(userRequestDto.getFirstName());
            customer.setLastName(userRequestDto.getLastName());
            customer.setUserName(userRequestDto.getUserName());
            customer.setPassword(userRequestDto.getPassword());
            customer.setEmail(userRequestDto.getEmail());
            customer.setAddress(userRequestDto.getAddress());
            customer.setStatus(UserStatus.NEW);
            customer.setRole(Role.USER);
            customer.setRegistrationDate(LocalDateTime.now());

            Customer savedCustomer = userRepository.save(customer);
            log.info("Customer entity saved successfully: {}", savedCustomer);
            user = savedCustomer;
        }

        // If the user type is EXPERT
        if (userType != null && userType.equals(UserType.EXPERT)) {
            log.info("User type EXPERT detected. Creating an Expert entity.");
            Expert expert = new Expert();
            expert.setFirstName(userRequestDto.getFirstName());
            expert.setLastName(userRequestDto.getLastName());
            expert.setUserName(userRequestDto.getUserName());
            expert.setPassword(userRequestDto.getPassword());
            expert.setEmail(userRequestDto.getEmail());
            expert.setAddress(userRequestDto.getAddress());

            log.info("Fetching sub-categories for Expert: {}", userRequestDto.getSubCategoryNames());
            List<String> subCategoryNames = userRequestDto.getSubCategoryNames();
            List<Category> categoryList = categoryService.findSubCategoriesByNames(subCategoryNames);

            if (categoryList == null || categoryList.isEmpty()) {
                log.error("Category validation failed. Could not find sub-categories by names: {}", subCategoryNames);
                throw new CustomException("Category is invalid", ErrorCode.CATEGORY_INVALID.getCode());
            }

            expert.setCategories(categoryList);
            expert.setStatus(UserStatus.NEW);
            expert.setRole(Role.USER);
            expert.setRegistrationDate(LocalDateTime.now());

            Expert savedExpert = userRepository.save(expert);
            log.info("Expert entity saved successfully: {}", savedExpert);
            user = savedExpert;
        }

        UserResponseDto responseDto = userEntityToUserResponseDtoConvertor.apply(user);
        responseDto.setRegistrationDate(user.getRegistrationDate());
        log.info("User successfully saved and converted to response DTO: {}", responseDto);
        return responseDto;
    }

    @Override
    public List<UserResponseDto> getALldUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> userList = userPage.getContent();
        return userEntityToUserResponseDtoConvertor.convertList(userList);
    }

    @Override
    public UserResponseDto deleteCategoryFromUser(String email, List<String> categoryNames) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        List<Category> shouldBeRemoveCategoryList = categoryService.findSubCategoriesByNames(categoryNames);
        Expert expert = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user instanceof Expert) {
                expert = (Expert) user;
                if (user.getStatus().equals(UserStatus.NEW)) {
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

    @Override
    public UserResponseDto addExpertToCategories(String email, List<String> categoryNames) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        List<Category> shouldBeAdd = categoryService.findSubCategoriesByNames(categoryNames);
        Expert expert = null;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user instanceof Expert) {
                expert = (Expert) user;
                if (user.getStatus().equals(UserStatus.NEW)) {
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

    @Override
    public List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable) {
        List<String> categoryNames = filterDto.categoryNames();
        List<Category> categoryList = categoryNames != null ? categoryService.findSubCategoriesByNames(categoryNames) : List.of();

        Specification<User> spec = UserSpecification.filterUsers(filterDto, categoryList);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<User> userList = userPage.getContent();
        return userEntityToUserResponseDtoConvertor.convertList(userList);
    }

    @Override
    public UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, oldPassword);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
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

    @Override
    public UserMessageDto updateStatusUser(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
                user.setStatus(UserStatus.APPROVED);
            }
            userRepository.save(user);
        } else {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }
        return new UserMessageDto("Status updated is approved");
    }

    @Override
    public UserMessageDto uploadImage(String email, MultipartFile image) {
        if (image.getSize() > 300 * 1024) {
            throw new CustomException("Image size must to be less than 300KB", ErrorCode.PHOTO_INVALID.getCode());
        }
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user instanceof Expert) {
                if (user.getStatus().equals(UserStatus.NEW)) {
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

    @Override
    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode()));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found with provided email", ErrorCode.NOT_FOUND.getCode()));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
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

    @Override
    public CreditResponseDto getBalance(String email, String password) {
        User user = findByEmailAndPassword(email, password);
        if (user == null) {
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }
        if (user.getStatus().equals(UserStatus.NEW)) {
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }
        BigDecimal balance = BigDecimal.valueOf(user.getCredit());
        return new CreditResponseDto(balance);
    }
}
