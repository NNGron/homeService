package ir.dto.order;

import ir.entity.Category;
import ir.entity.Customer;
import ir.entity.Offer;
import ir.entity.Review;
import ir.entity.enums.OrderStatus;

import java.util.List;

public class OrderResponseDto {

    private Double finalPrice;

    private String orderDate;

    private String orderDescription;

    private String executionDate;

    private String orderAddress;

    private Customer customer;

    private Category category;

    private OrderStatus status;

    private List<Offer> offers;

    private Review review;
}
