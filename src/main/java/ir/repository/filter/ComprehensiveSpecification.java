package ir.repository.filter;

import ir.dto.filter.ComprehensiveFilterDto;
import ir.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ComprehensiveSpecification {

    public static Specification<Order> filterComprehensive(ComprehensiveFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join relations
            Join<Order, Offer> offerJoin = root.join("offers", JoinType.LEFT);          // پیشنهاد‌ها ممکن است وجود نداشته باشد
            Join<Offer, Expert> expertJoin = offerJoin.join("expert", JoinType.LEFT);   // متخصصان ممکن است پیشنهاد نداشته باشند
            Join<Order, Category> categoryJoin = root.join("category", JoinType.INNER); // کتگوری همیشه موردنیاز است
            Join<Order, Review> reviewJoin = root.join("review", JoinType.LEFT);        // بازخوردها ممکن است وجود نداشته باشد
            Join<Order, Customer> customerJoin = root.join("customer", JoinType.INNER);

            // فیلتر بر اساس بازه زمانی `createdAt`
            if (filterDto.getCreatedAtStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filterDto.getCreatedAtStart()));
            }
            if (filterDto.getCreatedAtEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filterDto.getCreatedAtEnd()));
            }

            // فیلتر بر اساس بازه زمانی `updatedAt` (مخصوص سفارش‌های پرداخت‌شده)
            if (filterDto.getUpdatedAtStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), filterDto.getUpdatedAtStart()));
            }
            if (filterDto.getUpdatedAtEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), filterDto.getUpdatedAtEnd()));
            }

            // فیلتر بر اساس نام کتگوری
            if (filterDto.getCategoryName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")),
                        "%" + filterDto.getCategoryName().toLowerCase() + "%"));
            }

            // فیلتر بر اساس توضیحات کتگوری
            if (filterDto.getCategoryDescription() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("description")),
                        "%" + filterDto.getCategoryDescription().toLowerCase() + "%"));
            }

            // فیلتر بر اساس قیمت پایه کتگوری
            if (filterDto.getCategoryBasePrice() != null) {
                predicates.add(criteriaBuilder.equal(categoryJoin.get("basePrice"), filterDto.getCategoryBasePrice()));
            }

            // فیلتر بر اساس نام ساب‌کتگوری (دسته‌بندی والد)
            if (filterDto.getParentCategoryName() != null) {
                Join<Category, Category> parentCategoryJoin = categoryJoin.join("parentCategory", JoinType.INNER);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(parentCategoryJoin.get("name")),
                        "%" + filterDto.getParentCategoryName().toLowerCase() + "%"));
            }

            // فیلتر بر اساس Review (Rating و Comment)
            if (filterDto.getReviewRating() != null) {
                predicates.add(criteriaBuilder.equal(reviewJoin.get("rating"), filterDto.getReviewRating()));
            }
            if (filterDto.getReviewComment() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(reviewJoin.get("comment")),
                        "%" + filterDto.getReviewComment().toLowerCase() + "%"));
            }

            // User filters
            if (filterDto.getUserName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("userName")),
                        "%" + filterDto.getUserName().toLowerCase() + "%"));
            }
            if (filterDto.getFirstName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("firstName")),
                        "%" + filterDto.getFirstName().toLowerCase() + "%"));
            }
            if (filterDto.getLastName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("lastName")),
                        "%" + filterDto.getLastName().toLowerCase() + "%"));
            }
            if (filterDto.getEmail() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("email")),
                        "%" + filterDto.getEmail().toLowerCase() + "%"));
            }
            if (filterDto.getAddress() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("address")),
                        "%" + filterDto.getAddress().toLowerCase() + "%"));
            }

//            // فیلتر بر اساس UserType
//            if (filterDto.getUserType() != null) {
//                predicates.add(criteriaBuilder.equal(customerJoin.type(), filterDto.getUserType()));
//            }

            // Order filters
            if (filterDto.getFinalPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("finalPrice"), filterDto.getFinalPrice()));
            }
            if (filterDto.getOrderStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.getOrderStatus()));
            }
            if (filterDto.getOrderStartDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("orderDate"), filterDto.getOrderStartDate()));
            }
            if (filterDto.getStartWorkingTime() != null) {
                predicates.add(criteriaBuilder.equal(root.get("startWorkingTime"), filterDto.getStartWorkingTime()));
            }
            if (filterDto.getEndWorkingTime() != null) {
                predicates.add(criteriaBuilder.equal(root.get("endWorkingTime"), filterDto.getEndWorkingTime()));
            }
            if (filterDto.getStartWorkingDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("startWorkingDate"), filterDto.getStartWorkingDate()));
            }
            if (filterDto.getEndWorkingDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("endWorkingDate"), filterDto.getEndWorkingDate()));
            }
            if (filterDto.getExecutionDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("executionDate"), filterDto.getExecutionDate()));
            }
            if (filterDto.getOrderAddress() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("orderAddress")),
                        "%" + filterDto.getOrderAddress().toLowerCase() + "%"));
            }
            if (filterDto.getOrderDescription() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("orderDescription")),
                        "%" + filterDto.getOrderDescription().toLowerCase() + "%"));
            }
            if (filterDto.getOfferCustomerPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("offerCustomerPrice"), filterDto.getOfferCustomerPrice()));
            }

            // Offer filters
            if (filterDto.getProposedPrice() != null) {
                predicates.add(criteriaBuilder.equal(offerJoin.get("proposedPrice"), filterDto.getProposedPrice()));
            }
            if (filterDto.getEstimatedTime() != null) {
                predicates.add(criteriaBuilder.equal(offerJoin.get("estimatedTime"), filterDto.getEstimatedTime()));
            }
            //  if (filterDto.getStartTime() != null) {
            //                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(offerJoin.get("startTime")),
            //                        "%" + filterDto.getStartTime().toLowerCase() + "%"));
            //            }
            if (filterDto.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(offerJoin.get("startTime"), filterDto.getStartTime()));
            }

            // Expert filters (Average)
            if (filterDto.getExpertRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(expertJoin.get("rating"), filterDto.getExpertRating()));
            }

            // role filters
            if (filterDto.getRole() != null) {
                predicates.add(criteriaBuilder.equal(customerJoin.get("role"), filterDto.getRole()));
            }

            // فیلتر بر اساس UserStatus
            if (filterDto.getUserStatus() != null) {
                predicates.add(criteriaBuilder.equal(customerJoin.get("status"), filterDto.getUserStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
