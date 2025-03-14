package ir.dto.review;

import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.Order;

public record ReviewRequestDto(
        Integer rating,
        String comment,
        Order order,
        Customer customer,
        Expert expert) {

}