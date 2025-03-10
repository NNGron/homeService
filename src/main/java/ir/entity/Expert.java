package ir.entity;

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
    private String skills;
    private Double rating;
    private Double credit;

    @Lob
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "expert")
    private List<Offer> offers;
}
