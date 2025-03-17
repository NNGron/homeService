package ir.dto.review;

public record ReviewRequestDto(
        Integer rating,
        String comment,
        Long orderNumber,
        Long offerNumber) {
}