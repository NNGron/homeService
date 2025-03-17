package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public abstract class User extends BaseEntity {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private Double credit;

    @Enumerated(EnumType.STRING)
    private Role role;
}