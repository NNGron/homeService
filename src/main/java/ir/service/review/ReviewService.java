package ir.service.review;

import ir.dto.expert.ExpertReviewResponseDto;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.Review;

import java.util.List;

/**
 * Service interface for managing reviews.
 *
 * <p>This interface provides methods for adding reviews, retrieving reviews for experts,
 * and finding reviews based on expert IDs.</p>
 */
public interface ReviewService {

    ReviewResponseDto addReview(String token, ReviewRequestDto reviewRequestDto);

    /**
     * Finds all reviews for a specific expert based on their ID.
     *
     * @param expertId the unique ID of the expert
     * @return a list of {@link Review} entities for the expert
     */
    List<Review> findAllReviewsByExpertId(Long expertId);

    List<ExpertReviewResponseDto> getReviewsForExpert(String token);
}
