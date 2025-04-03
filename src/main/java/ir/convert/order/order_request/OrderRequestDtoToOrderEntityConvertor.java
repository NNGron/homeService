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
 * Converter to transform an {@link OrderRequestDto} into an {@link Order} entity.
 * This converter processes single request DTOs and lists of request DTOs into entities.
 */
@Slf4j
@Component
public class OrderRequestDtoToOrderEntityConvertor implements Function<OrderRequestDto, Order> {

    @Override
    public Order apply(OrderRequestDto carDto) {
        log.debug("Starting conversion of OrderRequestDto to Order entity");

        log.info("Successfully converted OrderRequestDto to Order entity");
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(carDto, Order.class);
    }

    public List<Order> convertList(List<OrderRequestDto> carDTOs) {
        log.debug("Starting batch conversion of OrderRequestDtos to Order entities");

        return carDTOs.stream()
                .map(this)
                .toList();
    }
}