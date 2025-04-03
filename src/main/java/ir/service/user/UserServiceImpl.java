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
import java.util.List;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final UserEntityToUserResponseDtoConvertor userEntityToUserResponseDtoConvertor;

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
        customer.setPassword(userRequestDto.getPassword());
        customer.setEmail(userRequestDto.getEmail());
        customer.setAddress(userRequestDto.getAddress());
        customer.setStatus(UserStatus.NEW);
        customer.setRole(Role.USER);

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
        admin.setPassword(userRequestDto.getPassword());
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

        if (!userRequestDto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            log.error("Validation failed: Password must contain at least one letter and one number. Email: {}", userRequestDto.getEmail());
            throw new CustomException("Password must contain at least one letter and one number", ErrorCode.INVALID_EMAIL_OR_PASSWORD.getCode());
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
    public boolean authenticateUser(String email, String password) {
        log.info("Authenticating user with email: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomException("No users found", ErrorCode.NO_USERS_FOUND.getCode())
        );

        if (password.equals(user.getPassword())) {
            log.info("Authentication successful for user: {}", email);
            return true;
        } else {
            log.warn("Authentication failed for user: {}", email);
            return false;
        }
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

    /**
     * Removes specific categories from an expert's associated categories.
     *
     * @param email         the expert's email for identification
     * @param categoryNames the names of the categories to be removed
     * @return the updated user as a {@link UserResponseDto}
     * @throws CustomException if validation fails or the user could not be updated
     */
    @Override
    public UserResponseDto deleteCategoryFromUser(String email, List<String> categoryNames) {
        log.info("Removing categories from user with email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }

        User user = optionalUser.get();
        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        if (!(user instanceof Expert)) {
            log.error("User with email {} is not an expert", email);
            throw new CustomException("User is not an expert", ErrorCode.NOT_FOUND.getCode());
        }

        Expert expert = (Expert) user;
        List<Category> shouldBeRemoveCategoryList = categoryService.findSubCategoriesByNames(categoryNames);
        List<Category> categories = expert.getCategories();

        categories.removeAll(shouldBeRemoveCategoryList);
        expert.setCategories(categories);

        expert = userRepository.save(expert);
        return userEntityToUserResponseDtoConvertor.apply(expert);
    }

    /**
     * Adds categories to an expert's profile.
     *
     * <p>This method checks if the user exists and is an expert. It ensures the user is approved
     * and does not already have the categories being added. If all validations pass, the new
     * categories are added to the expert's profile.</p>
     *
     * @param email the email of the expert
     * @param categoryNames a list of category names to add
     * @return the updated expert as a {@link UserResponseDto}
     * @throws CustomException if the user is not approved, not found, or if there are duplicate categories
     */
    @Override
    public UserResponseDto addExpertToCategories(String email, List<String> categoryNames) {
        log.info("Adding categories to expert with email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        List<Category> shouldBeAdd = categoryService.findSubCategoriesByNames(categoryNames);

        if (optionalUser.isEmpty() || !(optionalUser.get() instanceof Expert expert)) {
            log.error("User not found or is not an expert. Email: {}", email);
            throw new CustomException("User not found or is not an expert", ErrorCode.NOT_FOUND.getCode());
        }

        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with email {} is not approved", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        List<Category> existingCategories = expert.getCategories();
        List<String> duplicateCategories = shouldBeAdd.stream()
                .map(Category::getName)
                .filter(existingCategories.stream().map(Category::getName).toList()::contains)
                .toList();

        if (!duplicateCategories.isEmpty()) {
            log.error("Duplicate categories detected for expert with email {}: {}", email, duplicateCategories);
            throw new CustomException(
                    "Duplicate categories: " + duplicateCategories, ErrorCode.DUPLICATE_CATEGORY.getCode());
        }

        log.info("Adding new categories to expert with email: {}", email);
        existingCategories.addAll(shouldBeAdd);
        expert.setCategories(existingCategories);
        Expert updatedExpert = userRepository.save(expert);
        log.info("Categories successfully added to expert with email: {}", email);

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
     * @param email the user's email for authentication
     * @param oldPassword the user's current password
     * @param newPassword the new password to be set
     * @return a {@link UserMessageDto} confirming the password update
     * @throws CustomException if the user is not found or not approved
     */
    @Override
    public UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword) {
        log.info("Updating password for user with email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, oldPassword);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("User with email {} not found", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            user.setPassword(newPassword);
            userRepository.save(user);
        } else {
            log.error("User with email {} not found", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }
        log.info("Password updated successfully for user with email: {}", email);
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
        if (user.getStatus() == UserStatus.NEW) {
            user.setStatus(UserStatus.APPROVED);
            userRepository.save(user);
            log.info("Updated status to APPROVED for user: {}", user.getEmail());
        } else {
            log.warn("User status is not NEW. Current status: {}", user.getStatus());
        }

        return new UserMessageDto("User approved successfully");
    }


    /**
     * Uploads a profile image for the authenticated expert.
     *
     * @param email the expert's email for authentication
     * @param image the profile image to be uploaded
     * @return a {@link UserMessageDto} confirming the image upload
     * @throws CustomException if the image size exceeds the limit or upload fails
     */
    @Override
    public UserMessageDto uploadImage(String email, MultipartFile image) {
        log.info("Uploading profile image for expert with email: {}", email);

        if (image.getSize() > 300 * 1024) {
            log.error("Image size exceeds limit for email: {}", email);
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
        log.info("Profile image uploaded successfully for email: {}", email);
        return new UserMessageDto("Image uploaded successfully");
    }

    /**
     * Retrieves a user based on their email and password.
     *
     * <p>Validates the provided email and password and fetches the user from the database.
     * Throws an exception if the user is not found.</p>
     *
     * @param email the user's email for authentication
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
     * @param email the customer's email for authentication
     * @param amount the credit amount to be added
     * @return the updated customer as a {@link UserResponseDto}
     * @throws CustomException if the amount is invalid or the operation fails
     */
    @Override
    public UserResponseDto addCreditToCustomer(String email, BigDecimal amount) {
        log.info("Adding credit to customer with email: {}", email);

        User user = findByEmail(email);
        BigDecimal credit = user.getCredit() != null ? user.getCredit() : BigDecimal.ZERO;

        if (credit.add(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid amount provided for customer with email: {}", email);
            throw new CustomException("Amount is invalid", ErrorCode.INVALID_AMOUNT.getCode());
        }

        credit = credit.add(amount);
        user.setCredit(credit);
        user = saveUser(user);

        log.info("Credit added successfully to customer with email: {}", email);
        return userEntityToUserResponseDtoConvertor.apply(user);
    }

    /**
     * Retrieves the balance of the authenticated user.
     *
     * <p>Validates the user's authentication and ensures the user has an approved status
     * before returning the account balance.</p>
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @return the user's balance wrapped in a {@link CreditResponseDto}
     * @throws CustomException if the user is not found or is not approved
     */
    @Override
    public CreditResponseDto getBalance(String email, String password) {
        log.info("Retrieving balance for user with email: {}", email);

        User user = findByEmailAndPassword(email, password);
        if (user == null) {
            log.error("User not found with provided email: {}", email);
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }
        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }
        BigDecimal balance = user.getCredit() != null ? user.getCredit() : BigDecimal.ZERO;
        log.info("Successfully retrieved balance for user with email: {}. Balance: {}", email, balance);

        return new CreditResponseDto(balance);
    }

    /**
     * Soft deletes the authenticated user after validating associated orders and offers.
     *
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @throws CustomException if the user has non-paid orders or validation fails
     */
    @Override
    public void deleteUser(String email, String password) {
        log.info("Attempting to delete user with email: {}", email);

        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException(
                        "User not found with provided email and password",
                        ErrorCode.NOT_FOUND.getCode()));

        if (user instanceof Customer customer) {
            boolean allOrdersPaid = customer.getOrders().stream()
                    .allMatch(order -> OrderStatus.PAID.equals(order.getStatus()));

            if (!allOrdersPaid) {
                throw new CustomException("Cannot delete customer with non-paid orders",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
        }

        if (user instanceof Expert expert) {
            boolean allRelatedOrdersPaid = expert.getOffers().stream()
                    .map(Offer::getOrder)
                    .allMatch(order -> OrderStatus.PAID.equals(order.getStatus()));

            if (!allRelatedOrdersPaid) {
                throw new CustomException("Cannot delete expert with offers linked to non-paid orders",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
        }

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        log.info("Successfully soft-deleted user of type: {} with email: {}",
                user.getClass().getSimpleName(), email);
    }

    /**
     * Updates the details of the authenticated user.
     *
     * <p>Validates user authentication and updates fields like first name, last name,
     * address, and username. Ensures the user exists in the system before making changes.</p>
     *
     * @param userUpdateRequestDto the updated details to be applied to the user
     * @param email the user's email for authentication
     * @param password the user's password for authentication
     * @return the updated user as a {@link UserResponseDto}
     * @throws CustomException if the user is not found or validation fails
     */
    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, String email, String password) {
        log.info("Attempting to update user details for email: {}", email);

        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException(
                        "User not found with provided email and password",
                        ErrorCode.NOT_FOUND.getCode()));

        if (user.getStatus() == UserStatus.DELETED) {
            log.error("User with email {} is deleted and cannot be updated", email);
            throw new CustomException("User is deleted and cannot be updated", ErrorCode.UNAUTHORIZED.getCode());
        }

        log.info("Updating user details for email: {}", email);
        user.setFirstName(userUpdateRequestDto.getFirstName());
        user.setLastName(userUpdateRequestDto.getLastName());
        user.setAddress(userUpdateRequestDto.getAddress());
        user.setUserName(userUpdateRequestDto.getUserName());

        userRepository.save(user);
        log.info("User with email {} updated successfully", email);
        return userEntityToUserResponseDtoConvertor.apply(user);
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

        // Check for duplicates
        List<String> existingSubCategories = expert.getCategories().stream()
                .map(Category::getName)
                .toList();
        List<String> duplicates = subCategoryNames.stream()
                .filter(existingSubCategories::contains)
                .toList();

        if (!duplicates.isEmpty()) {
            log.error("Duplicate subcategories detected for expert with email {}: {}", expertEmail, duplicates);
            throw new CustomException("Duplicate subcategories: " + duplicates, ErrorCode.DUPLICATE_CATEGORY.getCode());
        }

        // Add new subcategories
        expert.getCategories().addAll(subCategories);
        userRepository.save(expert);

        log.info("Successfully added subcategories {} to expert with email {}", subCategoryNames, expertEmail);
    }
}