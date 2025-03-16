package ir.service.order;

import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Category;
import ir.entity.Order;
import ir.entity.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto saveOrder(OrderRequestDto orderDto,
                               String email,
                               String password);


    List<Order> findOrdersByCategories(List<Category> categories, OrderStatus orderStatus);

    List<OrderResponseDto> getOrder(String email, String password);
}
