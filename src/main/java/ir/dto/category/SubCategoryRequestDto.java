package ir.dto.category;

public record SubCategoryRequestDto(
        String name,
        String description,
        Double basePrice,
        String parentName) {
}
