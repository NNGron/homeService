package ir.dto.customer;

import ir.entity.Order;
import ir.entity.enums.UserStatus;

import java.util.List;

public class CustomerRequestDto {
    private String address;
    private Double credit;
    private UserStatus status;
    private List<Order> orders;
}
