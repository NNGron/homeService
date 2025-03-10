package ir.convert.order.orderRequest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.order.OrderRequestDto;
import ir.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class OrderRequestDtoToOrderEntityConvertor implements Function<OrderRequestDto, Order> {

    @Override
    public Order apply(OrderRequestDto carDto) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(carDto, Order.class);
    }

    public List<Order> convertList(List<OrderRequestDto> carDTOs) {
        return carDTOs.stream()
                .map(this)
                .toList();
    }
}