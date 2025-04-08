package ir.dto.order;

import ir.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderFilterDto(
        LocalDateTime orderStartDate,
        LocalDateTime orderEndDate,
        OrderStatus orderStatus,
        String categoryName,
        String subCategoryName
) {
}
