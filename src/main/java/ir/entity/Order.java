package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @PositiveOrZero(message = "Final price must be zero or a positive value")
    @Column(name = "final_price")
    private BigDecimal finalPrice;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Offer> offers;

    @OneToOne(mappedBy = "order")
    private Review review;

    @PositiveOrZero(message = "Offer Customer Price must be a positive value or zero")
    @Column(name = "offer_customer_price")
    private BigDecimal offerCustomerPrice;

    //LocalTime or LocalDateTime
    private LocalDateTime startWorkingTime;
    private LocalDateTime endWorkingTime;

    private LocalDate startWorkingDate;
    private LocalDate endWorkingDate;
}