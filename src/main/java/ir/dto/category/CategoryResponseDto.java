package ir.dto.category;

import ir.entity.Category;
import ir.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDto {
    private String name;
    private String description;
    private Double basePrice;
    private Category parentCategory;
    private List<Category> subCategories;
    private List<Order> orders;
}
