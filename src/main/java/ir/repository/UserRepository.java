package ir.repository;

import ir.entity.User;
import ir.entity.enums.Role;
import ir.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String username);

    Optional<User> findByEmailAndPassword(String email, String password);

    Page<User> findByStatusNotAndRoleNot(UserStatus status, Role role, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM users WHERE created_at BETWEEN :startDate " +
            "AND :endDate AND user_type = 'CUSTOMER'", nativeQuery = true)
    long countCustomersByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM users WHERE created_at BETWEEN :startDate " +
            "AND :endDate AND user_type = 'EXPERT'", nativeQuery = true)
    long countExpertsByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // تعداد سفارش‌هایی که مشتریان ثبت کرده‌اند (همه سفارش‌ها مرتبط به Customer)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer IS NOT NULL")
    long countOrdersByCustomers();

    // تعداد سفارش‌هایی که متخصصان تکمیل کرده‌اند
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED'")
    long countCompletedOrdersByExperts();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.email = :email " +
            "AND o.customer.createdAt BETWEEN :registrationStartDate AND :registrationEndDate")
    long countRequestsByEmailAndRegistrationDate(@Param("email") String email,
                                                 @Param("registrationStartDate") LocalDateTime registrationStartDate,
                                                 @Param("registrationEndDate") LocalDateTime registrationEndDate);

    @Query("SELECT COUNT(o) FROM Offer o WHERE o.expert.email = :email " +
            "AND o.order.status = 'COMPLETED' AND o.expert.createdAt BETWEEN :registrationStartDate " +
            "AND :registrationEndDate")
    long countCompletedOrdersByEmailAndRegistrationDate(@Param("email") String email,
                                                        @Param("registrationStartDate") LocalDateTime registrationStartDate,
                                                        @Param("registrationEndDate") LocalDateTime registrationEndDate);
}