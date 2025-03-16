package ir.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderRequestDto {

    private Double finalPrice;

    private LocalDateTime orderDate;

    @NotBlank(message = "Description must not be null")
    private String orderDescription;

    private LocalDateTime executionDate;

    private String orderAddress;

    private String subCategoryName;
}
