package ir.dto.category;

import ir.entity.Category;
import ir.entity.Order;

import java.util.List;

public class CategoryRequestDto {
    private String name;
    private String description;
    private Double basePrice;
    private Category parentCategory;
    private List<Category> subCategories;
    private List<Order> orders;
}
