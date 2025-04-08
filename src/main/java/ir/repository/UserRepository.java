package ir.repository;

import ir.entity.User;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String username);
    Optional<User> findByEmailAndPassword(String email, String password);
    Page<User> findByStatusNotAndRoleNot(UserStatus status, Role role, Pageable pageable);
}

