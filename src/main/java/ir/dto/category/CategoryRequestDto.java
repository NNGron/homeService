package ir.dto.category;

import ir.entity.Category;
import ir.entity.Order;

import java.util.List;


public record CategoryRequestDto(
        String name,
        String description,
        Double basePrice,
        Category parentCategory,
        List<Category> subCategories,
        List<Order> orders) {
}
