package ir.service.review;

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

    /**
     * Adds a new review for the authenticated user.
     *
     * @param email            the user's email for authentication
     * @param password         the user's password for authentication
     * @param reviewRequestDto the details of the review to be added
     * @return the added review as a {@link ReviewResponseDto}
     */
    ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto);

    /**
     * Finds all reviews for a specific expert based on their ID.
     *
     * @param expertId the unique ID of the expert
     * @return a list of {@link Review} entities for the expert
     */
    List<Review> findAllReviewsByExpertId(Long expertId);

    /**
     * Retrieves all reviews for the authenticated expert.
     *
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     * @return a list of {@link ReviewResponseDto} representing the expert's reviews
     */
    List<ReviewResponseDto> getReviewsForExpert(String email, String password);
}
