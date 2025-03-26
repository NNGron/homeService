package ir.convert.review;

import ir.dto.review.ReviewResponseDto;
import ir.entity.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class ReviewEntityToReviewResponseDtoConvertor implements Function<Review, ReviewResponseDto> {

    @Override
    public ReviewResponseDto apply(Review review) {
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
        return reviews.stream()
                .map(this)
                .toList();
    }
}
