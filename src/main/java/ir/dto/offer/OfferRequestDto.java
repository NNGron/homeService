package ir.dto.offer;

import ir.entity.Expert;
import ir.entity.Order;

public class OfferRequestDto {
    private Order order;
    private Expert expert;
    private Double proposedPrice;
    private Integer estimatedTime;
    private String startTime;
}
