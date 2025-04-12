package ir.dto.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.order.OrderResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferResponseDto {
    private Long offerNumber;
    private OrderResponseDto order;
    private String expertName;
    private String expertEmail;
    private BigDecimal proposedPrice;
    private Integer estimatedTime;
    private String availableTime;
    private Double expertRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
