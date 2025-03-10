package ir.dto.expert;

import ir.entity.Offer;
import ir.entity.enums.UserStatus;
import jakarta.persistence.Lob;

import java.util.List;

public class ExpertRequestDto {
    private String skills;
    private Double rating;
    private Double credit;
    @Lob
    private byte[] photo;
    private UserStatus status;
    private List<Offer> offers;
}
