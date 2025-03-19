package ir.service.review;

import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.Review;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto);

    List<Review> findAllReviewsByExpertId(Long expertId);
}
