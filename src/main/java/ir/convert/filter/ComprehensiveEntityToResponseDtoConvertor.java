package ir.convert.filter;

import ir.dto.filter.ComprehensiveResponseDto;
import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.Order;
import ir.entity.enums.UserType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Log4j2
@Component
public class ComprehensiveEntityToResponseDtoConvertor implements Function<Order, ComprehensiveResponseDto> {

    @Override
    public ComprehensiveResponseDto apply(Order order) {
        // تبدیل لیست پیشنهاد‌ها به OfferResponseDto
        List<OfferResponseDto> offers = order.getOffers().stream()
                .map(offer -> OfferResponseDto.builder()
                        .offerNumber(offer.getId())
                        .order(OrderResponseDto.builder()
                                .orderNumber(order.getId())
                                .orderDescription(order.getOrderDescription())
                                .build())
                        .expertName(offer.getExpert().getFirstName() + " " + offer.getExpert().getLastName())
                        .expertEmail(offer.getExpert().getEmail())
                        .proposedPrice(offer.getProposedPrice())
                        .estimatedTime(offer.getEstimatedTime())
                        .availableTime(offer.getAvailableTime())
                        .expertRating(offer.getExpert().getRating())
                        .createdAt(offer.getCreatedAt())
                        .updatedAt(offer.getUpdatedAt())
                        .build())
                .toList();


        // بررسی و تبدیل بازخورد‌ها به ReviewResponseDto
        ReviewResponseDto review = null;
        if (order.getReview() != null) {
            review = ReviewResponseDto.builder()
                    .rating(order.getReview().getRating())
                    .comment(order.getReview().getComment())
                    .orderNumber(order.getId()) // استفاده از شماره سفارش
                    .orderDescription(order.getOrderDescription())
                    .customerFirstName(order.getReview().getCustomer().getFirstName())
                    .customerLastName(order.getReview().getCustomer().getLastName())
                    .expertFirstName(order.getReview().getExpert().getFirstName())
                    .expertLastName(order.getReview().getExpert().getLastName())
                    .subCategoryName(order.getCategory().getParentCategory() != null
                            ? order.getCategory().getParentCategory().getName()
                            : null)
                    .build();
        }

        // بازگرداندن اطلاعات در ComprehensiveResponseDto
        return ComprehensiveResponseDto.builder()
                // اطلاعات مشتری
                .orderNumber(order.getId())
                .userName(order.getCustomer().getUserName())
                .firstName(order.getCustomer().getFirstName())
                .lastName(order.getCustomer().getLastName())
                .email(order.getCustomer().getEmail())
                .address(order.getCustomer().getAddress())
                .userStatus(order.getCustomer().getStatus())
                .role(order.getCustomer().getRole())
                .userType(UserType.valueOf(order.getCustomer().getClass().getSimpleName().toUpperCase())) // CUSTOMER یا EXPERT

                // اطلاعات سفارش
                .startWorkingTime(order.getStartWorkingTime())
                .endWorkingTime(order.getEndWorkingTime())
                .startWorkingDate(order.getStartWorkingDate())
                .endWorkingDate(order.getEndWorkingDate())
                .finalPrice(order.getFinalPrice())
                .orderDate(order.getOrderDate())
                .executionDate(order.getExecutionDate())
                .orderAddress(order.getOrderAddress())
                .orderStartDate(order.getCreatedAt())
                .orderEndDate(order.getUpdatedAt())
                .orderDescription(order.getOrderDescription())
                .orderStatus(order.getStatus())
                .offerCustomerPrice(order.getOfferCustomerPrice())

                // اطلاعات دسته‌بندی
                .categoryName(order.getCategory().getName())
                .categoryDescription(order.getCategory().getDescription())
                .categoryBasePrice(order.getCategory().getBasePrice())
                .parentCategoryName(order.getCategory().getParentCategory() != null
                        ? order.getCategory().getParentCategory().getName()
                        : null)

                // اطلاعات پیشنهاد‌ها
                .offers(offers)

                // اطلاعات بازخورد
                .reviews(review != null ? List.of(review) : List.of())

                // اطلاعات زمانی
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<ComprehensiveResponseDto> convertList(List<Order> orders) {
        log.debug("Starting batch conversion for {} Order entities", orders.size());

        List<ComprehensiveResponseDto> comprehensiveResponseDtos = orders.stream()
                .map(this)
                .toList();

        log.info("Successfully converted {} Order entities to DTOs", comprehensiveResponseDtos.size());
        return comprehensiveResponseDtos;
    }
}