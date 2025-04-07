package ir.dto.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ir.dto.order.OrderResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferResponseDto {
    private Long offerNumber;
    private OrderResponseDto order;
    private String expertName;
    private String expertEmail;
    private BigDecimal proposedPrice;
    private Integer estimatedTime;
    private String startTime;
    private Double expertRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
