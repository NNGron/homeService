package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Represents a category within the application.
 *
 * <p>The {@code Category} class is an entity that models the categorization of services, products,
 * or orders in the system. It provides hierarchical relationships, where each category can have
 * a parent category, and maintains associations with orders and experts.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Defines the name of the category with validation constraints for length and non-blank values.</li>
 *     <li>Includes an optional description of the category, with a maximum length of 500 characters.</li>
 *     <li>Stores a base price for services associated with the category, ensuring non-negative values.</li>
 *     <li>Implements a self-referencing relationship for hierarchical categorization using {@code parentCategory}.</li>
 *     <li>Associates with {@code Order} and {@code Expert} entities to represent relevant services and experts.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@code parentCategory} - A many-to-one relationship enabling hierarchical organization.</li>
 *     <li>{@code orders} - A one-to-many relationship representing orders linked to this category.</li>
 *     <li>{@code experts} - A many-to-many relationship representing experts skilled in this category.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code name} - Must be between 3 and 100 characters and cannot be blank.</li>
 *     <li>{@code description} - Cannot exceed 500 characters (optional field).</li>
 *     <li>{@code basePrice} - Must be zero or a positive value.</li>
 * </ul>
 *
 * <p>This class facilitates efficient categorization, ensuring hierarchical relationships
 * and associations with other entities.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category extends BaseEntity {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @PositiveOrZero(message = "Base price must be zero or a positive value")
    @Column(name = "base_price")
    private Double basePrice;

    // Self-referencing relationship for hierarchy
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "category")
    private List<Order> orders;

    @ManyToMany(mappedBy = "categories")
    private List<Expert> experts;
}