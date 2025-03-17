package ir.dto.offer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferRequestDto {
    private Long orderNumber;
    private Double proposedPrice;
    private Integer estimatedTime;
    private String startTime;
}
