package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the base class for all types of users in the system.
 *
 * <p>This abstract class defines the common attributes and functionalities
 * that are shared across all user types (customers, experts, and administrators).</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Defines common attributes like username, password, name, email, address, credit, status, and role.</li>
 *     <li>Implements validation constraints to ensure data integrity for each field.</li>
 *     <li>Utilizes the {@link Inheritance} annotation with the {@code JOINED} strategy to separate subclass tables
 *         while inheriting common columns.</li>
 *     <li>Supports logical deletion through {@link SQLDelete}, where the status is updated to 'DELETED'.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li><b>userName</b>: Must not be blank and must be unique within the database.</li>
 *     <li><b>password</b>: Must be at least 8 characters long and include at least one letter and one number.</li>
 *     <li><b>firstName</b> and <b>lastName</b>: Cannot be blank, ensuring proper user identification.</li>
 *     <li><b>email</b>: Must be a valid email format and unique across all users.</li>
 *     <li><b>address</b>: Cannot exceed 255 characters and must not be blank.</li>
 *     <li><b>credit</b>: Must be a non-negative value (i.e., zero or positive).</li>
 *     <li><b>status</b>: Determines the user's lifecycle state (NEW, APPROVED, or DELETED),
 *         represented by the {@link UserStatus} enum.</li>
 *     <li><b>role</b>: Defines the user's role (ADMIN, َUSER) using the {@link Role} enum.</li>
 * </ul>
 *
 * <p>Lifecycle Annotations:</p>
 * <ul>
 *     <li><b>{@link PrePersist}</b>: Automatically sets the registration date to the current timestamp before
 *         persisting a new user.</li>
 * </ul>
 *
 * <p>Inheritance Strategy:</p>
 * <ul>
 *     <li>Uses {@code InheritanceType.JOINED}, allowing all subclasses to have their own tables
 *         while sharing common attributes defined in this class.</li>
 * </ul>
 * @see ir.entity.enums.Role
 * @see ir.entity.enums.UserStatus
 * @see BaseEntity
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET status = 'DELETED' WHERE id = ?")
public abstract class User extends BaseEntity {

    @NotBlank(message = "Username must not be blank")
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must contain at least one letter and one number")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name must not be blank")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "address")
    private String address;

    @PositiveOrZero(message = "Credit must be a non-negative value")
    @Column(name = "credit")
    private BigDecimal credit;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
}