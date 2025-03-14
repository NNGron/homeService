package ir.repository;

import ir.dto.user.UserFilterDto;
import ir.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(UserFilterDto userFilterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!StringUtils.isEmpty(userFilterDto.userName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("userName")), "%" + userFilterDto.userName().toLowerCase() + "%"));

                // select * from Users u where u.username like % جیزی که کاربر وارد کرده بعنوان یوزرنیم%;
            }

            if (userFilterDto.firstName() != null && !userFilterDto.firstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + userFilterDto.firstName().toLowerCase() + "%"));
            }

            if (userFilterDto.lastName() != null && !userFilterDto.lastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + userFilterDto.lastName().toLowerCase() + "%"));
            }

            if (userFilterDto.email() != null && !userFilterDto.email().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + userFilterDto.email().toLowerCase() + "%"));
            }

            if (userFilterDto.role() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), userFilterDto.role()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
