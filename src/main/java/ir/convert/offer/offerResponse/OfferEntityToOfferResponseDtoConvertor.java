package ir.convert.offer.offerResponse;

import ir.convert.order.OrderResponse.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Offer;
import ir.entity.Order;
import ir.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class OfferEntityToOfferResponseDtoConvertor implements Function<Offer, OfferResponseDto> {

    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    @Override
    public OfferResponseDto apply(Offer offer) {
        OfferResponseDto offerResponseDto = new OfferResponseDto();
        User user = offer.getExpert();
        Order order = offer.getOrder();
        OrderResponseDto orderResponseDto = orderEntityToOrderResponseDtoConvertor.apply(order);
        offerResponseDto.setOfferNumber(offer.getId());
        offerResponseDto.setOrder(orderResponseDto);
        offerResponseDto.setExpertName(user.getFirstName());
        offerResponseDto.setExpertEmail(user.getEmail());
        offerResponseDto.setEstimatedTime(offer.getEstimatedTime());
        offerResponseDto.setStartTime(offer.getStartTime());
        offerResponseDto.setProposedPrice(offer.getProposedPrice());
        return offerResponseDto;
    }

    public List<OfferResponseDto> convertList(List<Offer> offers) {
        return offers.stream()
                .map(this)
                .toList();
    }

}
