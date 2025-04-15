package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long orderNumber;
    private LocalDateTime startWorkingTime;
    private LocalDateTime endWorkingTime;
    private BigDecimal finalPrice;
    private LocalDate orderDate;
    private String orderDescription;
    private String orderName;
    private LocalDate executionDate;
    private String orderAddress;
    private CategoryResponseDto mainCategory; // اضافه کردن Main Category
    private CategoryResponseDto subCategory; // اضافه کردن Sub Category
    private OrderStatus status;
    private List<OfferResponseDto> offers;
    private Review review;
    private BigDecimal offerCustomerPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate startWorkingDate;
    private LocalDate endWorkingDate;
}
