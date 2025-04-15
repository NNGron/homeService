package ir.service.user;

import ir.dto.credit.CreditResponseDto;
import ir.dto.user.*;
import ir.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing {@link User} entities and handling user-related operations.
 *
 * <p>This interface provides various methods for performing CRUD operations, updating user
 * statuses, managing passwords, uploading profile images, and associating users with
 * categories. It acts as a bridge between the controller layer and the data access layer.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Retrieve users with pagination or filtering options.</li>
 *     <li>Manage user categories for experts and customers.</li>
 *     <li>Update user credentials and statuses.</li>
 *     <li>Save new users and add credits to customer accounts.</li>
 * </ul>
 *
 * @see User
 * @see UserRequestDto
 * @see UserResponseDto
 * @see UserFilterDto
 * @see UserMessageDto
 */
public interface UserService {
    Optional<User> findByUserName(String username);

    LoginResponseDto login(String email, String password);

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable the pagination configuration specifying page number, size, and sorting
     * @return a list of {@link UserResponseDto} representing all users
     */
    List<UserResponseDto> getAllUsers(Pageable pageable);

    /**
     * Removes specific categories from an expert's associated categories.
     *
     * @param email         the expert's email for identification
     * @param categoryNames the names of the categories to be removed
     * @return the updated user as a {@link UserResponseDto}
     */
    UserResponseDto deleteExpertCategory(String token, String email, List<String> categoryNames);

    UserResponseDto addExpertToCategories(String token, List<String> categoryNames);

    /**
     * Retrieves filtered users based on the provided filter criteria and pagination configuration.
     *
     * @param filterDto the filter criteria for user retrieval
     * @param pageable the pagination configuration specifying page number, size, and sorting
     * @return a list of {@link UserResponseDto} representing filtered users
     */
    List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable);

    /**
     * Saves a new user to the system.
     *
     * @param userRequestDto the details of the user to be saved
     * @return the saved user as a {@link UserResponseDto}
     */
    UserResponseDto saveUser(UserRequestDto userRequestDto);

    /**
     * Updates the password for the authenticated user.
     *
     * @param email the user's email for authentication
     * @param oldPassword the current password of the user
     * @param newPassword the new password to be set
     * @return a {@link UserMessageDto} confirming the password update
     */
    UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword);

    UserMessageDto updateUserPassword(String token, String newPassword);
    /**
     * Updates the status of the authenticated user.
     *
     * @param email the user's email for identification
     * @return a {@link UserMessageDto} confirming the status update
     */
    UserMessageDto updateStatusUser(String email);

    UserMessageDto uploadImage(String token, MultipartFile image);

    /**
     * Retrieves a user based on their email and password.
     *
     * @param email the user's email for identification
     * @param password the user's password for authentication
     * @return the {@link User} entity matching the email and password
     */
    User findByEmailAndPassword(String email, String password);

    /**
     * Retrieves a user based on their email.
     *
     * @param email the user's email for identification
     * @return the {@link User} entity matching the email
     */
    User findByEmail(String email);

    /**
     * Saves the provided user entity to the database.
     *
     * @param user the {@link User} entity to be saved
     * @return the saved {@link User} entity
     */
    User saveUser(User user);

    /**
     * Adds credit to a customer's account.
     *
     * @param email the customer's email for authentication
     * @param amount the credit amount to be added
     * @return the updated customer as a {@link UserResponseDto}
     */
    UserResponseDto addCreditToCustomer(String email, BigDecimal amount);

    CreditResponseDto getBalance(String token);

    void deleteUser(String token);

    UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto,
                               String token);

    void authenticateAdmin(String email, String password);

    void addExpertToCategoriesByAdmin(String expertEmail, List<String> subCategoryNames);

    Map<String, Long> getRegistrationReport(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getExpertAndCustomerReport(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getExpertAndCustomerReportByEmail(String email, LocalDateTime registrationStartDate, LocalDateTime registrationEndDate);
}