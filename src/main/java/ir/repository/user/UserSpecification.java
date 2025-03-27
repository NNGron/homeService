package ir.repository.user;

import ir.dto.user.UserFilterDto;
import ir.entity.Category;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides dynamic filtering logic for querying users based on various attributes.
 *
 * <p>The {@code UserSpecification} class builds a dynamic {@link Specification}
 * for filtering {@link User} entities based on criteria provided in a {@link UserFilterDto}.
 * It also allows filtering users by their associated {@link Category} list.</p>
 *
 * <p>This specification is primarily used in conjunction with Spring Data JPA to
 * generate database queries at runtime, offering flexible filtering capabilities.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Dynamically builds {@link Predicate}s for filtering users based on:
 *         <ul>
 *             <li>First name</li>
 *             <li>Last name</li>
 *             <li>Email</li>
 *             <li>Associated categories</li>
 *         </ul>
 *     </li>
 *     <li>Handles null and empty values gracefully to ensure robust query generation.</li>
 *     <li>Allows joining with related entities such as {@link Category} to enable filtering based on associations.</li>
 * </ul>
 *
 * @see Specification
 * @see Predicate
 * @see UserFilterDto
 * @see User
 * @see Category
 */
public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDto userFilterDto, List<Category> categoryList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userFilterDto.firstName() != null && !userFilterDto.firstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + userFilterDto.firstName().toLowerCase() + "%"));
            }

            if (userFilterDto.lastName() != null && !userFilterDto.lastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + userFilterDto.lastName().toLowerCase() + "%"));
            }

            if (userFilterDto.email() != null && !userFilterDto.email().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + userFilterDto.email().toLowerCase() + "%"));
            }

            if (categoryList != null && !categoryList.isEmpty()) {
                Join<User, Category> categoryJoin = root.join("categories");
                predicates.add(categoryJoin.in(categoryList));
            }

            predicates.add(criteriaBuilder.notEqual(root.get("status"), UserStatus.DELETED));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
// select * from Users u where u.username like % جیزی که کاربر وارد کرده بعنوان یوزرنیم%;
