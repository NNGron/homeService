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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

/**
 * Represents a base class for all users in the system.
 *
 * <p>The {@code User} class defines common attributes and functionality shared
 * across all user types (e.g., customers, experts, admins). It uses the {@link Inheritance}
 * annotation with the {@code JOINED} strategy to enable inheritance in JPA, ensuring
 * that each subclass has its own table with shared columns inherited from this class.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Stores essential user information such as username, password, first and last name, email, and address.</li>
 *     <li>Maintains user-specific properties such as credit balance, status, and role.</li>
 *     <li>Ensures data integrity with detailed validation annotations for fields.</li>
 *     <li>Acts as a parent class for specific user roles (e.g., {@code Customer} and {@code Expert}).</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code userName} - Must not be blank and must be unique.</li>
 *     <li>{@code password} - Must be at least 8 characters long and contain at least one letter and one number.</li>
 *     <li>{@code firstName} - Must not be blank.</li>
 *     <li>{@code lastName} - Must not be blank.</li>
 *     <li>{@code email} - Must be a valid email address and unique.</li>
 *     <li>{@code address} - Must not exceed 255 characters and cannot be blank.</li>
 *     <li>{@code credit} - Must be zero or a positive value.</li>
 *     <li>{@code status} - Cannot be null and uses the {@link UserStatus} enumeration.</li>
 *     <li>{@code role} - Defines the user's role using the {@link Role} enumeration.</li>
 * </ul>
 *
 * <p>Inheritance Strategy:</p>
 * <ul>
 *     <li>Uses {@code InheritanceType.JOINED} to create separate tables for subclasses
 *     while sharing common fields defined in this class.</li>
 *     <li>Fields from {@code BaseEntity} (e.g., {@code id}) are also inherited.</li>
 * </ul>
 *
 * @Entity public class Customer extends User {
 * private String loyaltyCardNumber;
 * }
 * <p>This class provides the foundation for defining user-specific subclasses, ensuring
 * that all user types share a consistent structure and functionality.</p>
 */
@Inheritance(strategy = InheritanceType.JOINED)
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
    private Double credit;

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