package ir.convert.review;

import ir.dto.review.ReviewResponseDto;
import ir.entity.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;


/**
 * Converter to transform a {@link Review} entity into a {@link ReviewResponseDto}.
 * This class handles both single review conversions and batch conversions for lists of reviews.
 */
@Slf4j
@Component
public class ReviewEntityToReviewResponseDtoConvertor implements Function<Review, ReviewResponseDto> {

    @Override
    public ReviewResponseDto apply(Review review) {
        log.debug("Converting Review entity with ID: {}", review.getId());

        log.info("Successfully converted Review entity with ID: {}", review.getId());
        return ReviewResponseDto
                .builder()
                .orderNumber(review.getOrder().getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .customerFirstName(review.getCustomer().getFirstName())
                .customerLastName(review.getCustomer().getLastName())
                .expertFirstName(review.getExpert().getFirstName())
                .expertLastName(review.getExpert().getLastName())
                .orderDescription(review.getOrder().getOrderDescription())
                .subCategoryName(review.getOrder().getCategory().getName())
                .build();
    }

    public List<ReviewResponseDto> convertList(List<Review> reviews) {
        log.debug("Starting batch conversion for {} Review entities", reviews.size());

        log.info("Successfully converted {} Review entities to DTOs", reviews.size());
        return reviews.stream()
                .map(this)
                .toList();
    }
}
