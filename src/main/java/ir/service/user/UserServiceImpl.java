package ir.service.user;

import ir.convert.user.UserEntityToUserResponseDtoConvertor;
import ir.dto.credit.CreditResponseDto;
import ir.dto.user.*;
import ir.entity.*;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import ir.entity.enums.UserType;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.UserRepository;
import ir.repository.filter.UserSpecification;
import ir.security.JwtTokenProvider;
import ir.service.VerificationService;
import ir.service.category.CategoryService;
import ir.service.offer.OfferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for managing {@link User} entities.
 *
 * <p>This class handles user-related operations such as registration, validation,
 * category association, and retrieval of users. It ensures proper validation and
 * logging for all operations.</p>
 */
@AllArgsConstructor
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;
    private final OfferService offerService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;
    private final VerificationService verificationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        return userRepository.findByUserName(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUserName(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))))
                .orElseThrow(() -> new CustomException("User not found", ErrorCode.NOT_FOUND.getCode()));
    }

    /**
     * Registers a new user and saves their details to the database.
     *
     * <p>Validates the user's data and creates either a {@link Customer} or {@link Expert}
     * entity based on the provided user type.</p>
     *
     * @param userRequestDto the details of the user to be registered
     * @return the registered user as a {@link UserResponseDto}
     * @throws CustomException if validation fails or the user type is invalid
     */
    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        log.info("Starting user registration: {}", userRequestDto);
        log.info("Validating user data for email: {}", userRequestDto.getEmail());
        checkValidation(userRequestDto);

        log.info("Creating a new user with type: {}", userRequestDto.getUserType());
        User user = null;
        UserType userType = userRequestDto.getUserType();

        if (userType != null && userType.equals(UserType.CUSTOMER)) {
            user = createCustomer(userRequestDto);
        }
        if (userType != null && userType.equals(UserType.EXPERT)) {
            user = createExpert(userRequestDto);
        }
        if (userType != null && userType.equals(UserType.ADMIN)) {
            user = createAdmin(userRequestDto);
        }

        if (!(user instanceof Admin)) {
            verificationService.createVerificationTokenAndSendEmail(user);
        } else {
            user = userRepository.save(user);
        }

        UserResponseDto responseDto = userEntityToUserResponseDtoConvertor.apply(user);
        log.info("User successfully saved and converted to response DTO: {}", responseDto);
        return responseDto;
    }

    /**
     * Creates and saves an expert entity based on the provided user request data.
     *
     * @param userRequestDto the details of the expert to be created
     * @return the saved {@link User} entity
     * @throws CustomException if category validation fails or the expert could not be saved
     */
    private User createExpert(UserRequestDto userRequestDto) {
        log.info("User type EXPERT detected. Creating an Expert entity.");
        Expert expert = new Expert();

        expert.setFirstName(userRequestDto.getFirstName());
        expert.setLastName(userRequestDto.getLastName());
        expert.setUserName(userRequestDto.getUserName());
        if (!userRequestDto.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,}$")) {
            throw new CustomException("Password is invalid", ErrorCode.PASSWORD_INVALID.getCode());
        }
        expert.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        expert.setEmail(userRequestDto.getEmail());
        expert.setAddress(userRequestDto.getAddress());

        log.info("Fetching sub-categories for Expert: {}", userRequestDto.getSubCategoryNames());
        List<String> subCategoryNames = userRequestDto.getSubCategoryNames();

        if (subCategoryNames == null || subCategoryNames.isEmpty()) {
            throw new CustomException("Subcategories must not be empty", ErrorCode.CATEGORY_INVALID.getCode());
        }

        List<Category> categoryList = categoryService.findSubCategoriesByNames(subCategoryNames);
        if (categoryList == null || categoryList.isEmpty()) {
            log.error("Category validation failed. Could not find sub-categories by names: {}", subCategoryNames);
            throw new CustomException("Category is invalid", ErrorCode.CATEGORY_INVALID.getCode());
        }

        Wallet wallet = new Wallet();
        wallet.setOwner(expert);
        wallet.setCredit(BigDecimal.ZERO);
        expert.setWallet(wallet);
        expert.setCategories(categoryList);
        expert.setStatus(UserStatus.NEW);
        expert.setRole(Role.USER);

        User savedExpert = userRepository.save(expert);
        log.info("Expert entity saved successfully: {}", savedExpert);
        return savedExpert;
    }

    /**
     * Creates and saves a customer entity based on the provided user request data.
     *
     * @param userRequestDto the details of the customer to be created
     * @return the saved {@link User} entity
     * @throws CustomException if validation fails or the customer could not be saved
     */
    private User createCustomer(UserRequestDto userRequestDto) {
        log.info("User type CUSTOMER detected. Creating a Customer entity.");
        User customer = new Customer();
        customer.setFirstName(userRequestDto.getFirstName());
        customer.setLastName(userRequestDto.getLastName());
        customer.setUserName(userRequestDto.getUserName());
        customer.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        customer.setEmail(userRequestDto.getEmail());
        customer.setAddress(userRequestDto.getAddress());
        customer.setStatus(UserStatus.NEW);
        customer.setRole(Role.USER);
        Wallet wallet = new Wallet();
        wallet.setOwner(customer);
        wallet.setCredit(BigDecimal.ZERO);
        customer.setWallet(wallet);
        customer = userRepository.save(customer);
        log.info("Customer entity saved successfully: {}", customer);
        return customer;
    }

    private User createAdmin(UserRequestDto userRequestDto) {

        log.info("User type ADMIN detected. Creating a admin entity.");
        User admin = new Admin();
        admin.setFirstName(userRequestDto.getFirstName());
        admin.setLastName(userRequestDto.getLastName());
        admin.setUserName(userRequestDto.getUserName());
        admin.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        admin.setEmail(userRequestDto.getEmail());
        admin.setAddress(userRequestDto.getAddress());
        admin.setRole(Role.ADMIN);

        admin = userRepository.save(admin);
        log.info("Admin entity saved successfully: {}", admin);
        return admin;
    }

    /**
     * Validates the provided user request data.
     *
     * <p>Checks if required fields are present, the password format is valid, and
     * the email does not already exist in the system.</p>
     *
     * @param userRequestDto the user request data to validate
     * @throws CustomException if validation fails or the email already exists
     */
    private void checkValidation(UserRequestDto userRequestDto) {
        log.info("Validating required fields for user: {}", userRequestDto.getEmail());

        if (StringUtils.isEmpty(userRequestDto.getEmail()) || StringUtils.isEmpty(userRequestDto.getPassword())) {
            log.error("Validation failed: Email or Password is empty. UserRequestDto: {}", userRequestDto);
            throw new CustomException("Email and Password must not be empty", ErrorCode.INVALID_EMAIL_OR_PASSWORD.getCode());
        }

        Optional<User> optionalUser = userRepository.findByEmailAndPassword(
                userRequestDto.getEmail(), userRequestDto.getPassword());

        log.info("Checking if email already exists: {}", userRequestDto.getEmail());
        if (optionalUser.isPresent()) {
            log.error("Email already exists: {}", userRequestDto.getEmail());
            throw new CustomException("Email already exist", ErrorCode.ALREADY_EXIST.getCode());
        }
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public LoginResponseDto login(String email, String password) {
        log.info("Attempting to authenticate user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "User not found with email: " + email, ErrorCode.NO_USERS_FOUND.getCode()));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(
                    "Invalid password for email: " + email, ErrorCode.INVALID_EMAIL_OR_PASSWORD.getCode());
        }

        Role role = user.getRole();
        UserStatus userStatus = user.getStatus();

        // ADMIN
        if (role.equals(Role.ADMIN)) {
            if (userStatus.equals(UserStatus.DELETED)) {
                throw new CustomException(
                        "Admin account is deleted and cannot log in.", ErrorCode.USER_DELETED.getCode());
            }
        }

        // USER
        else if (role.equals(Role.USER)) {
            if (!userStatus.equals(UserStatus.APPROVED)) {
                throw new CustomException(
                        "Your account is not approved and you cannot log in.", ErrorCode.USER_NOT_APPROVED.getCode());
            }
        } else {
            throw new CustomException("Unsupported role.", ErrorCode.INVALID_USER_ROLE.getCode());
        }

        // تولید توکن JWT برای کاربر
        UserDetails userDetails = loadUserByUsername(user.getUserName());
        String token = jwtTokenProvider.generateToken(userDetails.getUsername());

        log.info("User {} successfully logged in.", email);

        // بازگشت پاسخ ورود
        return new LoginResponseDto("Logged in successfully", userDetails.getUsername(), token);
    }

    /**
     * Retrieves a paginated list of users, excluding those with the ADMIN role.
     *
     * <p>This method fetches all users whose status is not DELETED and role is not ADMIN.
     * If no users are found matching the criteria, a custom exception is thrown.</p>
     *
     * @param pageable the pagination details, including page size and sorting options
     * @return a list of {@link UserResponseDto} representing the filtered users
     * @throws CustomException if no users are found
     */
    @Override
    public List<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("Fetching all non-admin users with pagination details: {}", pageable);

        Page<User> userPage = userRepository.findByStatusNotAndRoleNot(UserStatus.DELETED, Role.ADMIN, pageable);

        if (userPage == null || userPage.getContent().isEmpty()) {
            log.error("No users found excluding admin roles.");
            throw new CustomException("No users found", ErrorCode.NO_USERS_FOUND.getCode());
        }

        log.info("Successfully fetched {} users excluding admin roles.", userPage.getContent().size());
        return userEntityToUserResponseDtoConvertor.convertList(userPage.getContent());
    }

    @Override
    public UserResponseDto deleteExpertCategory(String token, String email, List<String> categoryNames) {
        log.info("Validating user and deleting categories from expert with email: {}", email);

        // Step 1: Validate token and user role
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty()) {
            log.error("User with token not found.");
            throw new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
        }

        User user = userOptional.get();
        if (!(user instanceof Admin || user instanceof Expert)) {
            log.error("Unauthorized access by user: {}", username);
            throw new CustomException("Access denied. Only Admin or Expert can perform this operation.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 2: Proceed to delete categories for the expert
        Optional<User> expertOptional = userRepository.findByEmail(email);
        if (expertOptional.isEmpty()) {
            log.error("Expert with email {} not found.", email);
            throw new CustomException("Expert not found.", ErrorCode.NOT_FOUND.getCode());
        }

        User expertUser = expertOptional.get();
        if (!(expertUser instanceof Expert)) {
            log.error("User is not an expert. Email: {}", email);
            throw new CustomException("User is not an expert.", ErrorCode.USER_IS_NOT_EXPERT.getCode());
        }

        Expert expert = (Expert) expertUser;
        List<Category> categoriesToRemove = categoryService.findSubCategoriesByNames(categoryNames);
        expert.getCategories().removeAll(categoriesToRemove);
        userRepository.save(expert);

        log.info("Successfully removed categories for expert with email: {}", email);
        return userEntityToUserResponseDtoConvertor.apply(expert);
    }

    @Override
    public UserResponseDto addExpertToCategories(String token, List<String> categoryNames) {
        log.info("Validating expert and adding categories.");

        // Step 1: Validate user using the token
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty() || !(userOptional.get() instanceof Expert expert)) {
            log.error("Unauthorized access or user not found. Username: {}", username);
            throw new CustomException("Access denied or user not found.", ErrorCode.NOT_FOUND.getCode());
        }

        // Step 2: Check expert status
        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with username {} is not approved.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 3: Retrieve categories to add
        List<Category> categoriesToAdd = categoryService.findSubCategoriesByNames(categoryNames);

        // Validate all categories are found in the database
        List<String> invalidCategories = categoryNames.stream()
                .filter(name -> categoriesToAdd.stream()
                        .noneMatch(category -> category.getName().equals(name))).toList();

        if (!invalidCategories.isEmpty()) {
            log.error("Invalid categories detected: {}", invalidCategories);
            throw new CustomException(
                    "Invalid categories: " + invalidCategories, ErrorCode.INVALID_CATEGORY.getCode());
        }
        List<Category> existingCategories = expert.getCategories();

        // Step 4: Check for duplicate categories
        List<String> duplicateCategories = categoriesToAdd.stream()
                .map(Category::getName)
                .filter(existingCategories.stream().map(Category::getName).toList()::contains)
                .toList();

        if (!duplicateCategories.isEmpty()) {
            log.error("Duplicate categories detected for expert with username {}: {}", username, duplicateCategories);
            throw new CustomException(
                    "Duplicate categories: " + duplicateCategories, ErrorCode.DUPLICATE_CATEGORY.getCode());
        }

        // Step 5: Add new categories to the expert
        log.info("Adding new categories to expert with username: {}", username);
        existingCategories.addAll(categoriesToAdd);
        expert.setCategories(existingCategories);

        // Step 6: Save the updated expert entity
        Expert updatedExpert = userRepository.save(expert);
        log.info("Categories successfully added to expert with username: {}", username);

        return userEntityToUserResponseDtoConvertor.apply(updatedExpert);
    }

    /**
     * Retrieves filtered users based on the provided criteria and pagination settings.
     *
     * @param filterDto the filter criteria for user retrieval
     * @param pageable  the pagination configuration specifying page number, size, and sorting
     * @return a list of {@link UserResponseDto} representing filtered users
     */
    @Override
    public List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable) {
        log.info("Fetching filtered users with criteria: {}", filterDto);

        List<String> categoryNames = filterDto.categoryNames();
        List<Category> categoryList =
                categoryNames != null ? categoryService.findSubCategoriesByNames(categoryNames) : List.of();

        Specification<User> spec = UserSpecification.filterUsers(filterDto, categoryList);
        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<User> userList = userPage.getContent();

        log.info("Successfully retrieved {} filtered users.", userList.size());
        return userEntityToUserResponseDtoConvertor.convertList(userList);
    }

    /**
     * Updates the password for the authenticated user.
     *
     * @param email       the user's email for authentication
     * @param oldPassword the user's current password
     * @param newPassword the new password to be set
     * @return a {@link UserMessageDto} confirming the password update
     * @throws CustomException if the user is not found or not approved
     */
    @Override
    public UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword) {
        log.info("Updating password for user with email: {}", email);

        String encodeOldPass = passwordEncoder.encode(oldPassword);
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, encodeOldPass);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("User with email {} not found", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }
        log.info("Password updated successfully for user with email: {}", email);
        return new UserMessageDto("Password updated successfully");
    }

    @Override
    public UserMessageDto updateUserPassword(String token, String newPassword) {
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);
        userOptional.ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

        });
        return new UserMessageDto("Password updated successfully");
    }

    /**
     * Updates the status of the specified user.
     *
     * @param email the user's email for identification
     * @return a {@link UserMessageDto} confirming the status update
     * @throws CustomException if the user is already deleted or validation fails
     */
    @Override
    public UserMessageDto updateStatusUser(String email) {
        log.info("Starting updateStatusUser for email: {}", email);

        // Fetch user from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", ErrorCode.NOT_FOUND.getCode()));
        log.debug("Fetched user: {}", user);

        // Update status logic
        if (user.getStatus() == UserStatus.PENDING_APPROVAL) {
            user.setStatus(UserStatus.APPROVED);
            userRepository.save(user);
            log.info("Updated status to APPROVED for user: {}", user.getEmail());
            return new UserMessageDto("User approved successfully");
        } else {
            log.warn("User status is not PENDING_APPROVAL. Current status: {}", user.getStatus());
            throw new CustomException("User should be in pending approval step.", ErrorCode.INVALID_DATA.getCode());
        }

    }

    @Override
    public UserMessageDto uploadImage(String token, MultipartFile image) {
        log.info("Uploading profile image for expert with token.");

        // Step 1: Validate image size
        if (image.getSize() > 300 * 1024) {
            log.error("Image size exceeds limit for token.");
            throw new CustomException("Image size must be less than 300KB.", ErrorCode.PHOTO_INVALID.getCode());
        }

        // Step 2: Extract username from token and validate user
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty() || !(userOptional.get() instanceof Expert)) {
            log.error("User not found or is not an expert. Username: {}", username);
            throw new CustomException("User not found or not an expert.", ErrorCode.NOT_FOUND.getCode());
        }

        Expert expert = (Expert) userOptional.get();

        // Step 3: Validate user's status
        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with username {} is not approved.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 4: Update the profile image
        try {
            expert.setPhoto(image.getBytes());
            userRepository.save(expert);
            log.info("Profile image uploaded successfully for username: {}", username);
        } catch (IOException e) {
            log.error("Failed to upload image for username: {}", username, e);
            throw new CustomException("Cannot upload image.", ErrorCode.IMAGE_CAN_NOT_UPLOAD.getCode());
        }

        return new UserMessageDto("Image uploaded successfully.");
    }


    /**
     * Retrieves a user based on their email and password.
     *
     * <p>Validates the provided email and password and fetches the user from the database.
     * Throws an exception if the user is not found.</p>
     *
     * @param email    the user's email for authentication
     * @param password the user's password for authentication
     * @return the {@link User} entity matching the provided email and password
     * @throws CustomException if the user is not found with the provided credentials
     */
    @Override
    public User findByEmailAndPassword(String email,
                                       String password) {
        log.info("Attempting to find user with email: {}", email);

        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException(
                        "User not found with provided email and password", ErrorCode.NOT_FOUND.getCode()));
    }

    /**
     * Retrieves a user based on their email address.
     *
     * <p>Fetches the user from the database using the provided email. If no user is found,
     * throws an exception with an appropriate error message.</p>
     *
     * @param email the user's email for identification
     * @return the {@link User} entity matching the provided email
     * @throws CustomException if no user is found with the provided email
     */
    @Override
    public User findByEmail(String email) {
        log.info("Attempting to find user with email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "User not found with provided email", ErrorCode.NOT_FOUND.getCode()));
    }

    /**
     * Saves the given user entity to the database.
     *
     * <p>This method persists the user entity in the database and returns the saved entity.
     * Ensures proper handling of any exceptions during the save operation.</p>
     *
     * @param user the {@link User} entity to be saved
     * @return the saved {@link User} entity
     */
    @Override
    public User saveUser(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Adds credit to the specified customer's account.
     *
     * @param email  the customer's email for authentication
     * @param amount the credit amount to be added
     * @return the updated customer as a {@link UserResponseDto}
     * @throws CustomException if the amount is invalid or the operation fails
     */
    @Override
    public UserResponseDto addCreditToCustomer(String email, BigDecimal amount) {
        log.info("Adding credit to customer with email: {}", email);

        User user = findByEmail(email);
        BigDecimal credit = user.getWallet().getCredit() != null ? user.getWallet().getCredit() : BigDecimal.ZERO;

        if (credit.add(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid amount provided for customer with email: {}", email);
            throw new CustomException("Amount is invalid", ErrorCode.INVALID_AMOUNT.getCode());
        }

        credit = credit.add(amount);
        user.getWallet().setCredit(credit);
        user = saveUser(user);

        log.info("Credit added successfully to customer with email: {}", email);
        return userEntityToUserResponseDtoConvertor.apply(user);
    }

    @Override
    public CreditResponseDto getBalance(String token) {
        log.info("Retrieving balance for user with token");

        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty()) {
            log.error("User with token not found.");
            throw new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
        }
        User user = userOptional.get();
        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with username {} is not approved.", username);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }
        BigDecimal balance = user.getWallet().getCredit() != null ? user.getWallet().getCredit() : BigDecimal.ZERO;
        log.info("Successfully retrieved balance for user with username: {}. Balance: {}", username, balance);

        return new CreditResponseDto(balance);
    }

    @Override
    public void deleteUser(String token) {
        log.info("Attempting to delete user using token.");

        // Step 1: Extract username from token and validate user
        String username = jwtTokenProvider.extractUsername(token);
        User user = userRepository.findByUserName(username).orElseThrow(() -> {
            log.error("User with username {} not found.", username);
            return new CustomException("User not found with provided token.", ErrorCode.NOT_FOUND.getCode());
        });

        // Step 2: Handle specific rules for customers
        if (user instanceof Customer customer) {
            boolean allOrdersPaid = customer.getOrders().stream()
                    .allMatch(order -> OrderStatus.PAID.equals(order.getStatus()));

            if (!allOrdersPaid) {
                log.error("Cannot delete customer with username {} due to non-paid orders.", username);
                throw new CustomException(
                        "Cannot delete customer with non-paid orders.", ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
        }

        // Step 3: Handle specific rules for experts
        if (user instanceof Expert expert) {
            boolean allRelatedOrdersPaid = expert.getOffers().stream()
                    .map(Offer::getOrder).allMatch(order -> OrderStatus.PAID.equals(order.getStatus()));

            if (!allRelatedOrdersPaid) {
                log.error("Cannot delete expert with username {} due to linked non-paid orders.", username);
                throw new CustomException(
                        "Cannot delete expert with offers linked to non-paid orders.", ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
        }

        // Step 4: Soft delete the user by updating their status
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        log.info("Successfully soft-deleted user of type: {} with username: {}",
                user.getClass().getSimpleName(), username);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, String token) {
        log.info("Attempting to update user using token.");

        // Step 1: Extract username from token and validate user
        String username = jwtTokenProvider.extractUsername(token);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User with username {} not found.", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        if (user.getStatus() == UserStatus.DELETED) {
            log.error("User with token is deleted and cannot be updated");
            throw new CustomException("User is deleted and cannot be updated", ErrorCode.UNAUTHORIZED.getCode());
        }

        log.info("Updating details for user with username: {}", username);
        user.setFirstName(userUpdateRequestDto.getFirstName());
        user.setLastName(userUpdateRequestDto.getLastName());
        user.setAddress(userUpdateRequestDto.getAddress());
//        user.setUserName(userUpdateRequestDto.getUserName());

        User updatedUser = userRepository.save(user);

        log.info("Successfully updated user with username: {}", username);
        return userEntityToUserResponseDtoConvertor.apply(updatedUser);
    }

    /**
     * Authenticates an admin using email and password.
     *
     * @param email    the admin's email
     * @param password the admin's password
     * @throws CustomException if the admin is not found or authentication fails
     */
    @Override
    public void authenticateAdmin(String email, String password) {
        log.info("Authenticating admin with email: {}", email);

        User admin = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> {
                    log.error("Admin authentication failed for email: {}", email);
                    return new CustomException("Invalid admin credentials", ErrorCode.UNAUTHORIZED.getCode());
                });

        if (!admin.getRole().equals(Role.ADMIN)) {
            log.error("User with email {} is not an admin", email);
            throw new CustomException("User is not authorized as admin", ErrorCode.UNAUTHORIZED.getCode());
        }

        log.info("Admin with email {} authenticated successfully", email);
    }

    /**
     * Adds subcategories to an expert's profile.
     *
     * @param expertEmail      the email of the expert
     * @param subCategoryNames the list of subcategories to add
     * @throws CustomException if the expert is not found or validation fails
     */
    @Override
    public void addExpertToCategoriesByAdmin(String expertEmail, List<String> subCategoryNames) {
        log.info("Fetching expert with email: {}", expertEmail);

        User user = userRepository.findByEmail(expertEmail)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", expertEmail);
                    return new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
                });

        if (!(user instanceof Expert expert)) {
            log.error("User with email {} is not an expert", expertEmail);
            throw new CustomException("User is not an expert", ErrorCode.NOT_FOUND.getCode());
        }

        log.info("Fetching and validating subcategories: {}", subCategoryNames);
        List<Category> subCategories = categoryService.findSubCategoriesByNames(subCategoryNames);

        List<String> invalidSubCategories = subCategoryNames.stream()
                .filter(name -> subCategories.stream().noneMatch(category -> category.getName().equals(name)))
                .toList();

        if (!invalidSubCategories.isEmpty()) {
            log.error("Invalid subcategories detected: {}", invalidSubCategories);
            throw new CustomException("Invalid subcategories: " + invalidSubCategories, ErrorCode.INVALID_CATEGORY.getCode());
        }

        // Check for duplicates
        List<String> existingSubCategories = expert.getCategories().stream().map(Category::getName).toList();
        List<String> duplicates = subCategoryNames.stream().filter(existingSubCategories::contains).toList();

        if (!duplicates.isEmpty()) {
            log.error("Duplicate subcategories detected for expert with email {}: {}", expertEmail, duplicates);
            throw new CustomException(
                    "Duplicate subcategories: " + duplicates, ErrorCode.DUPLICATE_CATEGORY.getCode());
        }

        // Add new subcategories
        expert.getCategories().addAll(subCategories);
        userRepository.save(expert);

        log.info("Successfully added subcategories {} to expert with email {}", subCategoryNames, expertEmail);
    }

    @Override
    public Map<String, Long> getRegistrationReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> report = new HashMap<>();

        long customerRegistrations = userRepository.countCustomersByRegistrationDateBetween(startDate, endDate);
        long expertRegistrations = userRepository.countExpertsByRegistrationDateBetween(startDate, endDate);

        report.put("CustomerRegistrations", customerRegistrations);
        report.put("ExpertRegistrations", expertRegistrations);

        return report;
    }

    @Override
    public Map<String, Object> getExpertAndCustomerReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();

        long customerRegistrations = userRepository.countCustomersByRegistrationDateBetween(startDate, endDate);

        long expertRegistrations = userRepository.countExpertsByRegistrationDateBetween(startDate, endDate);

        long customerOrders = userRepository.countOrdersByCustomers();

        long completedOrdersByExperts = userRepository.countCompletedOrdersByExperts();

        report.put("CustomerRegistrations", customerRegistrations);
        report.put("ExpertRegistrations", expertRegistrations);
        report.put("CustomerOrders", customerOrders);
        report.put("CompletedOrdersByExperts", completedOrdersByExperts);

        return report;
    }

    @Override
    public Map<String, Object> getExpertAndCustomerReportByEmail(String email,
                                                                 LocalDateTime registrationStartDate,
                                                                 LocalDateTime registrationEndDate) {
        Map<String, Object> report = new HashMap<>();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user instanceof Customer) {
                long requests = userRepository.countRequestsByEmailAndRegistrationDate(
                        email, registrationStartDate, registrationEndDate);
                report.put("Requests", requests);

            } else if (user instanceof Expert) {
                long completedOrders = userRepository.countCompletedOrdersByEmailAndRegistrationDate(
                        email, registrationStartDate, registrationEndDate);
                report.put("CompletedOrders", completedOrders);

                long expertOffers = offerService.countOffersByExpertEmail(email);
                report.put("ExpertOffers", expertOffers);

            } else {
                report.put("Message", "The email does not belong to a valid customer or expert.");
            }
        } else {
            report.put("Message", "User not found with email: " + email);
        }
        return report;
    }
}