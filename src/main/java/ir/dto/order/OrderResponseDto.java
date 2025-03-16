package ir.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.category.CategoryResponseDto;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private Long orderNumber;

    private Double finalPrice;

    private LocalDateTime orderDate;

    private String orderDescription;

    private LocalDateTime executionDate;

    private String orderAddress;

    private CategoryResponseDto categoryResponseDto;

    private OrderStatus status;

    private List<Offer> offers;

    private Review review;
}
