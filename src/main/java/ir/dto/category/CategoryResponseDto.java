package ir.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.dto.order.OrderResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDto {
    private CategoryResponseDto parentCategory;
    private String name;
    private String description;
    private Double basePrice;
    //    private List<CategoryResponseDto> subCategories;
    private List<OrderResponseDto> orders;
}
