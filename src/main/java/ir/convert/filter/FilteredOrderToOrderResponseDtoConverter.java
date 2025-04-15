package ir.convert.filter;

import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Offer;
import ir.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilteredOrderToOrderResponseDtoConverter {

    /**
     * Converts a single Order entity to OrderResponseDto, including its offers and experts.
     *
     * @param order the Order entity to convert
     * @return the converted OrderResponseDto
     */
    public OrderResponseDto convert(Order order) {
        // Convert offers to OfferResponseDtos
        List<OfferResponseDto> offerDtos = convertOffers(order.getOffers());

        // Convert Order to OrderResponseDto
        return OrderResponseDto.builder()
                .orderNumber(order.getId())
                .startWorkingDate(order.getStartWorkingDate())
                .endWorkingDate(order.getEndWorkingDate())
                .startWorkingTime(order.getStartWorkingTime())
                .endWorkingTime(order.getEndWorkingTime())
                .orderDescription(order.getOrderDescription())
                .orderDate(order.getOrderDate())
                .executionDate(order.getExecutionDate())
                .orderAddress(order.getOrderAddress())
                .status(order.getStatus())
                .finalPrice(order.getFinalPrice())
                .offerCustomerPrice(order.getOfferCustomerPrice())
                .offers(offerDtos) // Add offers
                .build();
    }

    /**
     * Converts a list of Orders to a list of OrderResponseDtos.
     *
     * @param orders the list of Order entities to convert
     * @return the list of converted OrderResponseDtos
     */
    public List<OrderResponseDto> convertList(List<Order> orders) {
        return orders.stream().map(this::convert).toList();
    }

    /**
     * Converts a list of Offer entities to a list of OfferResponseDtos, including expert details.
     *
     * @param offers the list of Offer entities to convert
     * @return the list of converted OfferResponseDtos
     */
    private List<OfferResponseDto> convertOffers(List<Offer> offers) {
        return offers.parallelStream().map(offer -> OfferResponseDto.builder()
                        .offerNumber(offer.getId())
                        .proposedPrice(offer.getProposedPrice())
                        .estimatedTime(offer.getEstimatedTime())
                        .expertName(offer.getExpert().getFirstName())
                        .expertEmail(offer.getExpert().getEmail())
                        .expertRating(offer.getExpert().getRating())
                        .build())
                .collect(Collectors.toList());
    }
}
