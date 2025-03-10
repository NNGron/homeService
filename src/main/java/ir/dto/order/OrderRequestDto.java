package ir.dto.order;

import ir.entity.Category;
import ir.entity.Customer;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequestDto {

    @NotNull(message = "Final price must not be null")
    @Min(value = 0, message = "Final price must be a positive value")
    private Double finalPrice;

    private String orderDate;

    @NotBlank(message = "Description must not be null")
    private String orderDescription;

    private String executionDate;

    private String orderAddress;

    private Customer customer;

    private Category category;

    private OrderStatus status;

    private List<Offer> offers;

    private Review review;
}
