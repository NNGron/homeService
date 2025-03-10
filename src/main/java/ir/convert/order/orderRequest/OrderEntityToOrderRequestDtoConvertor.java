package ir.convert.order.orderRequest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.order.OrderRequestDto;
import ir.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderEntityToOrderRequestDtoConvertor implements Function<Order, OrderRequestDto> {

    @Override
    public OrderRequestDto apply(Order order) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(order, OrderRequestDto.class);
    }

    public List<OrderRequestDto> convertList(List<Order> orders) {
        return orders.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
