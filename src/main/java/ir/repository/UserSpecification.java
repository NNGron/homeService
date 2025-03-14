package ir.repository;

import ir.dto.user.UserFilterDto;
import ir.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDto userFilterDto) {
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

            if (!StringUtils.isEmpty(userFilterDto.skills())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")), "%" + userFilterDto.skills().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
// select * from Users u where u.username like % جیزی که کاربر وارد کرده بعنوان یوزرنیم%;
