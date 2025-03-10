package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review extends BaseEntity {

    private Integer rating;
    private String comment;

    @OneToOne
    private Order order;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Expert expert;

}