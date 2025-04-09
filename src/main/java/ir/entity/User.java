package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name = "users")
//@SQLDelete(sql = "UPDATE users SET status = 'DELETED' WHERE id = ?")
public abstract class User extends BaseEntity {

    @NotBlank(message = "Username must not be blank")
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must have at least 8 characters")
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

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private BankCard card;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}