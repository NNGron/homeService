package ir.service.order;

import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;

public interface OrderService {

    OrderResponseDto saveOrder(OrderRequestDto orderDto,
                               String email,
                               String password);
}
