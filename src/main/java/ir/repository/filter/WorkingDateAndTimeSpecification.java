package ir.repository.filter;

import ir.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class WorkingDateAndTimeSpecification {

    public static Specification<Order> filterOrdersByWorkingDate(LocalDate startWorkingDate, LocalDateTime endWorkingTime) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // فیلتر بر اساس بازه تاریخ شروع
            if (startWorkingDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("startWorkingDate"), startWorkingDate));
            }

            // فیلتر بر اساس بازه زمان پایان
            if (endWorkingTime != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("endWorkingTime"), endWorkingTime));
            }

            return predicate;
        };
    }
}
