package ir.convert.order.order_response;

import ir.dto.order.OrderResponseDto;
import ir.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Converter to transform an {@link Order} entity into an {@link OrderResponseDto}.
 * This class handles both single entity conversions and batch conversions for lists of orders.
 */
@Slf4j
@Component
public class OrderEntityToOrderResponseDtoConvertor implements Function<Order, OrderResponseDto> {

    @Override
    public OrderResponseDto apply(Order order) {
        log.debug("Converting Order entity with ID: {}", order.getId());

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderNumber(order.getId());
        orderResponseDto.setStartWorkingDate(order.getStartWorkingDate());
        orderResponseDto.setEndWorkingDate(order.getEndWorkingDate());
        orderResponseDto.setStartWorkingTime(order.getStartWorkingTime());
        orderResponseDto.setEndWorkingTime(order.getEndWorkingTime());
        orderResponseDto.setOrderDescription(order.getOrderDescription());
        orderResponseDto.setOrderName(order.getCategory().getName());
        orderResponseDto.setOrderAddress(order.getOrderAddress());
        orderResponseDto.setOrderDate(order.getOrderDate());
        orderResponseDto.setExecutionDate(order.getExecutionDate());
        orderResponseDto.setStatus(order.getStatus());
        orderResponseDto.setFinalPrice(order.getFinalPrice());
        orderResponseDto.setOfferCustomerPrice(order.getOfferCustomerPrice());
        orderResponseDto.setCreatedAt(order.getCreatedAt());
        orderResponseDto.setUpdatedAt(order.getUpdatedAt());

        log.info("Successfully converted Order entity with ID: {}", order.getId());
        return orderResponseDto;
    }

    public List<OrderResponseDto> convertList(List<Order> orders) {
        log.debug("Starting batch conversion for {} Order entities", orders.size());

        log.info("Successfully converted {} Order entities to DTOs", orders.size());
        return orders.stream()
                .map(this)
                .toList();
    }
}
