package ir.convert.offer;

import ir.convert.order.order_response.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Expert;
import ir.entity.Offer;
import ir.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Converter to map an {@link Offer} entity to an {@link OfferResponseDto}.
 * This converter is responsible for transforming the domain model into a DTO
 * that can be used in responses.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OfferEntityToOfferResponseDtoConvertor implements Function<Offer, OfferResponseDto> {

    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    @Override
    public OfferResponseDto apply(Offer offer) {
        log.debug("Converting Offer entity with ID: {}", offer.getId());

        OfferResponseDto offerResponseDto = new OfferResponseDto();
        Expert user = offer.getExpert();
        Order order = offer.getOrder();
        OrderResponseDto orderResponseDto = orderEntityToOrderResponseDtoConvertor.apply(order);
        offerResponseDto.setOfferNumber(offer.getId());
        offerResponseDto.setOrder(orderResponseDto);
        offerResponseDto.setExpertName(user.getFirstName());
        offerResponseDto.setExpertEmail(user.getEmail());
        offerResponseDto.setEstimatedTime(offer.getEstimatedTime());
        offerResponseDto.setAvailableTime(offer.getAvailableTime());
        offerResponseDto.setProposedPrice(offer.getProposedPrice());
        offerResponseDto.setExpertRating(user.getRating());
        offerResponseDto.setCreatedAt(offer.getCreatedAt());
        offerResponseDto.setUpdatedAt(offer.getUpdatedAt());

        log.info("Successfully converted Offer with ID: {}", offer.getId());
        return offerResponseDto;
    }

    public List<OfferResponseDto> convertList(List<Offer> offers) {
        log.debug("Starting conversion of {} Offer entities", offers.size());

        log.info("Successfully converted {} Offer entities to DTOs", offers.size());
        return offers.stream()
                .map(this)
                .toList();
    }
}