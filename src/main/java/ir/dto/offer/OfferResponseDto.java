package ir.dto.offer;

import ir.dto.order.OrderResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferResponseDto {
    private Long offerNumber;
    private OrderResponseDto order;
    private String expertName;
    private String expertEmail;
    private Double proposedPrice;
    private Integer estimatedTime;
    private String startTime;
}
