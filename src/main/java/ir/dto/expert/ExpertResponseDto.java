package ir.dto.expert;

import ir.entity.Offer;
import ir.entity.enums.UserStatus;

import java.util.List;

public class ExpertResponseDto {
    private String skills;
    private Double rating;
    private Double credit;
    private byte[] photo;
    private UserStatus status;
    private List<Offer> offers;
}
