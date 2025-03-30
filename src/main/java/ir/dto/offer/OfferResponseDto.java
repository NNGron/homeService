package ir.dto.offer;

import ir.dto.order.OrderResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OfferResponseDto {
    private Long offerNumber;
    private OrderResponseDto order;
    private String expertName;
    private String expertEmail;
    private BigDecimal proposedPrice;
    private Integer estimatedTime;
    private String startTime;
    private Double expertRating;
}
