package ir.dto.expert;

import ir.entity.Offer;
import ir.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ExpertResponseDto {
    private String skills;
    private Double rating;
    private Double credit;
    private byte[] photo;
    private UserStatus status;
    private List<Offer> offers;
}
