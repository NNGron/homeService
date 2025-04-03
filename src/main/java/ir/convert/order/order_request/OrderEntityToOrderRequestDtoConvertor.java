package ir.convert.order.order_request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.order.OrderRequestDto;
import ir.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Converter to transform an {@link Order} entity into an {@link OrderRequestDto}.
 * This converter handles both single order entities and lists of order entities.
 */
@Slf4j
@Component
public class OrderEntityToOrderRequestDtoConvertor implements Function<Order, OrderRequestDto> {

    @Override
    public OrderRequestDto apply(Order order) {
        log.debug("Starting conversion of Order entity to OrderRequestDto");

        log.info("Successfully converted Order entity with ID: {}", order.getId());
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(order, OrderRequestDto.class);
    }

    public List<OrderRequestDto> convertList(List<Order> orders) {
        log.debug("Starting batch conversion of Order entities to OrderRequestDto");

        log.info("Successfully converted {} Order entities to DTOs", orders.size());
        return orders.stream()
                .map(this)
                .toList();
    }
}
