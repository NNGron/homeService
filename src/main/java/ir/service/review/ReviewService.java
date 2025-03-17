package ir.service.review;

import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;

public interface ReviewService {
    ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto);
}
