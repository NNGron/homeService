package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents an order placed by a customer within the system.
 *
 * <p>The {@code Order} class models all necessary information related to an order,
 * such as its pricing, dates, associated customer, and related entities like
 * {@link Category}, {@link Offer}, and {@link Review}. It serves as a central
 * entity for managing customer requests and order fulfillment.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Defines details like final price, order date, execution date, address, and description.</li>
 *     <li>Associates with a customer through a {@code ManyToOne} relationship.</li>
 *     <li>Links to a category, indicating the type of service associated with the order.</li>
 *     <li>Tracks the status of the order using {@link OrderStatus} enumeration.</li>
 *     <li>Includes relationships with offers and reviews for comprehensive order management.</li>
 * </ul>
 *
 * <p>Relationships:</p>
 * <ul>
 *     <li>{@code customer} - Links the order to the customer who placed it.</li>
 *     <li>{@code category} - Represents the category of services for the order.</li>
 *     <li>{@code offers} - A one-to-many relationship connecting the order to offers made by experts.</li>
 *     <li>{@code review} - A one-to-one relationship linking the order to its associated review.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *     <li>{@code finalPrice} - Must be zero or a positive value.</li>
 *     <li>{@code orderDate} - Cannot be null.</li>
 *     <li>{@code executionDate} - Cannot be null and must be a present or future date.</li>
 *     <li>{@code orderAddress} - Must not exceed 255 characters and cannot be blank.</li>
 *     <li>{@code orderDescription} - Must not exceed 500 characters and cannot be blank.</li>
 *     <li>{@code status} - Cannot be null and must be one of the predefined statuses in {@link OrderStatus}.</li>
 * </ul>
 *
 * <p>This class is essential for modeling and managing orders, ensuring all necessary
 * details and relationships are encapsulated within a single entity.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @PositiveOrZero(message = "Final price must be zero or a positive value")
    @Column(name = "final_price")
    private Double finalPrice;

    @NotNull(message = "Order date must not be null")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @NotNull(message = "Execution date must not be null")
    @FutureOrPresent(message = "Execution date must be in the present or future")
    @Column(name = "execution_date")
    private LocalDate executionDate;

    @NotBlank(message = "Order address must not be blank")
    @Size(max = 255, message = "Order address must not exceed 255 characters")
    @Column(name = "order_address", length = 255)
    private String orderAddress;

    @NotBlank(message = "Order description must not be blank")
    @Size(max = 500, message = "Order description must not exceed 500 characters")
    @Column(name = "order_description", length = 500)
    private String orderDescription;

    @NotNull(message = "Customer must not be null")
    @ManyToOne
    private Customer customer;

    @NotNull(message = "Category must not be null")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Category category;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Offer> offers;

    @OneToOne(mappedBy = "order")
    private Review review;

    @PositiveOrZero(message = "Offer Customer Price must be a positive value or zero")
    @Column(name = "offer_customer_price")
    private Double offerCustomerPrice;

}
