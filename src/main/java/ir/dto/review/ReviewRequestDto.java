package ir.dto.review;

import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.Order;

public class ReviewRequestDto {
    private Integer rating;
    private String comment;
    private Order order;
    private Customer customer;
    private Expert expert;
}
