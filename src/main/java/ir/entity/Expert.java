package ir.entity;

import ir.convert.ByteArrayBlobConverter;
import ir.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "experts")
public class Expert extends User {

    private Double rating;
    private Double credit;

    @Lob
    @Convert(converter = ByteArrayBlobConverter.class)
    @Column(name = "photo", nullable = true)
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "expert")
    private List<Offer> offers;

    @ManyToMany
    @JoinTable(
            name = "expert_category",
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

}
