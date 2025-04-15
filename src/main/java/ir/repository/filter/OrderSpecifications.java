package ir.repository.filter;

import ir.dto.filter.FilterOrderByWorkingDateDto;
import ir.entity.Order;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderSpecifications {

    public Specification<Order> filterOrdersWithOffersAndExpertsByWorkingDate(FilterOrderByWorkingDateDto filterOrderByWorkingDateDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join آفرها و متخصصان مرتبط
            root.fetch("offers", JoinType.LEFT).fetch("expert", JoinType.LEFT);

            // فیلتر بازه شروع
            if (filterOrderByWorkingDateDto.getStartWorkingDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startWorkingDate"),
                        filterOrderByWorkingDateDto.getStartWorkingDate()
                ));
            }

            // فیلتر بازه پایان
            if (filterOrderByWorkingDateDto.getEndWorkingDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endWorkingDate"),
                        filterOrderByWorkingDateDto.getEndWorkingDate()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
