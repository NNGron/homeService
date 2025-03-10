package ir.entity;

import ir.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Offer extends BaseEntity {
    @ManyToOne
    private Order order;
    
    @ManyToOne
    private Expert expert;
    
    private Double proposedPrice;
    private Integer estimatedTime;
    private String startTime;
}