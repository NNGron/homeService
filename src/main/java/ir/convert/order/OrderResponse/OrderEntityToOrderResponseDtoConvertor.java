package ir.convert.order.OrderResponse;

import ir.dto.order.OrderResponseDto;
import ir.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class OrderEntityToOrderResponseDtoConvertor implements Function<Order, OrderResponseDto> {


    @Override
    public OrderResponseDto apply(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderNumber(order.getId());
        orderResponseDto.setOrderDescription(order.getOrderDescription());
        orderResponseDto.setOrderName(order.getCategory().getName());
        orderResponseDto.setOrderAddress(order.getOrderAddress());
        orderResponseDto.setOrderDate(order.getOrderDate());
        orderResponseDto.setExecutionDate(order.getExecutionDate());
        orderResponseDto.setStatus(order.getStatus());
        orderResponseDto.setFinalPrice(order.getFinalPrice());
        return orderResponseDto;
    }


    public List<OrderResponseDto> convertList(List<Order> orders) {
        return orders.stream()
                .map(this)
                .toList();
    }
}
