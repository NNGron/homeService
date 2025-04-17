package ir.repository.filter;

import ir.dto.user.UserFilterDto;
import ir.entity.Category;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDto userFilterDto, List<Category> categoryList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // فیلتر بر اساس نام
            if (userFilterDto.firstName() != null && !userFilterDto.firstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + userFilterDto.firstName().toLowerCase() + "%"));
            }

            if (userFilterDto.lastName() != null && !userFilterDto.lastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + userFilterDto.lastName().toLowerCase() + "%"));
            }

            // فیلتر بر اساس ایمیل
            if (userFilterDto.email() != null && !userFilterDto.email().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + userFilterDto.email().toLowerCase() + "%"));
            }

            // فیلتر بر اساس نوع کاربر
            if (userFilterDto.userType() != null) {
                predicates.add(criteriaBuilder.equal(root.type(), userFilterDto.userType().name()));
            }

            // فیلتر بر اساس امتیاز
            if (userFilterDto.rating() != null) {
                predicates.add(criteriaBuilder.equal(root.get("rating"), userFilterDto.rating()));
            }

            // فیلتر بر اساس کتگوری‌ها
            if (categoryList != null && !categoryList.isEmpty()) {
                Join<User, Category> categoryJoin = root.join("categories");
                predicates.add(categoryJoin.in(categoryList));
            }

            // فیلتر بر اساس تاریخ ثبت‌نام
            if (userFilterDto.registrationStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"), userFilterDto.registrationStartDate()));
            }

            // حذف کاربران حذف‌شده
            predicates.add(criteriaBuilder.notEqual(root.get("status"), UserStatus.DELETED));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
// select * from Users u where u.username like % جیزی که کاربر وارد کرده بعنوان یوزرنیم%;
