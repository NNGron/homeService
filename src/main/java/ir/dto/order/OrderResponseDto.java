package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long orderNumber;
    private BigDecimal finalPrice;
    private LocalDate orderDate;
    private String orderDescription;
    private String orderName;
    private LocalDate executionDate;
    private String orderAddress;
    private CategoryResponseDto categoryResponseDto;
    private OrderStatus status;
    private List<Offer> offers;
    private Review review;
    private BigDecimal offerCustomerPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
