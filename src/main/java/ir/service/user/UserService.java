package ir.service.user;

import ir.dto.credit.CreditResponseDto;
import ir.dto.user.UserFilterDto;
import ir.dto.user.UserMessageDto;
import ir.dto.user.UserRequestDto;
import ir.dto.user.UserResponseDto;
import ir.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable The {@link Pageable} object defining pagination and sorting.
     * @return A list of {@link UserResponseDto} objects representing the users.
     */
    List<UserResponseDto> getALldUsers(Pageable pageable);

    /**
     * Removes categories from a user's profile based on their email address.
     *
     * @param email         The email address of the user.
     * @param categoryNames A list of category names to remove from the user.
     * @return A {@link UserResponseDto} containing updated details of the user.
     */
    UserResponseDto deleteCategoryFromUser(String email, List<String> categoryNames);

    /**
     * Assigns an expert user to specific categories based on their email.
     *
     * @param email         The email address of the expert.
     * @param categoryNames A list of category names to assign to the expert.
     * @return A {@link UserResponseDto} containing updated details of the user.
     */
    UserResponseDto addExpertToCategories(String email, List<String> categoryNames);

    /**
     * Retrieves a filtered and paginated list of users based on the provided criteria.
     *
     * @param filterDto The {@link UserFilterDto} object containing filtering criteria.
     * @param pageable  The {@link Pageable} object defining pagination and sorting.
     * @return A list of {@link UserResponseDto} objects matching the filter criteria.
     */
    List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable);

    /**
     * Saves a new user based on the provided user details.
     *
     * @param userRequestDto The {@link UserRequestDto} object containing user details.
     * @return A {@link UserResponseDto} containing details of the saved user.
     */
    UserResponseDto saveUser(UserRequestDto userRequestDto);

    /**
     * Updates a user's password based on the provided old and new passwords.
     *
     * @param email       The email address of the user.
     * @param oldPassword The user's current password.
     * @param newPassword The new password to set.
     * @return A {@link UserMessageDto} containing a success message or details of any issues.
     */
    UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword);

    /**
     * Updates the status of a user based on their email address.
     *
     * @param email The email address of the user.
     * @return A {@link UserMessageDto} containing a message about the status update.
     */
    UserMessageDto updateStatusUser(String email);

    /**
     * Uploads a profile image for a user based on their email address.
     *
     * @param email The email address of the user.
     * @param image The {@link MultipartFile} containing the user's image.
     * @return A {@link UserMessageDto} containing a success message or details of any issues.
     */
    UserMessageDto uploadImage(String email, MultipartFile image);

    /**
     * Finds a {@link User} entity based on their email address and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return The {@link User} entity if found.
     */
    User findByEmailAndPassword(String email, String password);

    /**
     * Finds a {@link User} entity based on their email address.
     *
     * @param email The email address of the user.
     * @return The {@link User} entity if found.
     */
    User findByEmail(String email);

    /**
     * Saves a {@link User} entity to the database.
     *
     * @param user The {@link User} entity to save.
     * @return The saved {@link User} entity.
     */
    User saveUser(User user);

    /**
     * Adds credit to a customer's account based on their email address.
     *
     * @param email  The email address of the customer.
     * @param amount The amount of credit to add.
     * @return A {@link UserResponseDto} containing updated details of the customer.
     */
    UserResponseDto addCreditToCustomer(String email, double amount);

    CreditResponseDto getBalance(String email, String password);
}
