package ir.entity;

import ir.entity.base.BaseEntity;
import ir.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    private Double finalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime executionDate;
    private String orderAddress;
    private String orderDescription;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<Offer> offers;

    @OneToOne(mappedBy = "order")
    private Review review;
}
