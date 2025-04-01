package ir.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.order.OrderResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDto {
    private String name;
    private String description;
    private BigDecimal basePrice;
    private List<OrderResponseDto> orders;
    private CategoryResponseDto parentCategory;
}
