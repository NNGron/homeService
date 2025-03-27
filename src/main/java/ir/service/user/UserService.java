package ir.service.user;

import ir.dto.credit.CreditResponseDto;
import ir.dto.user.*;
import ir.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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

    List<UserResponseDto> getALldUsers(Pageable pageable);

    UserResponseDto deleteCategoryFromUser(String email, List<String> categoryNames);

    UserResponseDto addExpertToCategories(String email, List<String> categoryNames);

    List<UserResponseDto> getFilteredUsers(UserFilterDto filterDto, Pageable pageable);

    UserResponseDto saveUser(UserRequestDto userRequestDto);

    UserMessageDto updateUserPassword(String email, String oldPassword, String newPassword);

    UserMessageDto updateStatusUser(String email);

    UserMessageDto uploadImage(String email, MultipartFile image);

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User saveUser(User user);

    UserResponseDto addCreditToCustomer(String email, BigDecimal amount);

    CreditResponseDto getBalance(String email, String password);

    void deleteUser(String email, String password);

    UserResponseDto updateUser(UserUpdateRequestDto userUpdateRequestDto, String email, String password);
}
