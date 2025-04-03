package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.expert.ExpertReviewResponseDto;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/comment")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<ReviewResponseDto> saveComment(@RequestParam String email,
                                                         @RequestParam String password,
                                                         @RequestBody ReviewRequestDto reviewRequestDto) {
        log.info("Received request to submit a comment for customer with email: {}", email);
        log.debug("Review details provided by customer: {}", reviewRequestDto);

        ReviewResponseDto reviewResponseDto = reviewService.addReview(email, password, reviewRequestDto);

        log.info("Successfully saved comment for customer with email: {}", email);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @GetMapping("/expert-comments")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<List<ExpertReviewResponseDto>> getReviewsForExpert(@RequestParam String email,
                                                                             @RequestParam String password) {
        log.info("Received request to fetch reviews for expert with email: {}", email);

        List<ExpertReviewResponseDto> reviews = reviewService.getReviewsForExpert(email, password);

        log.info("Successfully fetched {} reviews for expert with email: {}", reviews.size(), email);
        return ResponseEntity.ok(reviews);
    }
}