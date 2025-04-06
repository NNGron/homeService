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
    public ResponseEntity<ReviewResponseDto> saveComment(@RequestHeader("Authorization") String token,
                                                         @RequestBody ReviewRequestDto reviewRequestDto) {
        log.info("Received request to submit a comment for customer with token");
        log.debug("Review details provided by customer: {}", reviewRequestDto);

        ReviewResponseDto reviewResponseDto = reviewService.addReview(token, reviewRequestDto);

        log.info("Successfully saved comment for customer with token");
        return ResponseEntity.ok(reviewResponseDto);
    }

    @GetMapping("/expert-comments")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<List<ExpertReviewResponseDto>> getReviewsForExpert(@RequestHeader("Authorization") String token) {
        log.info("Received request to fetch reviews for expert with token");

        List<ExpertReviewResponseDto> reviews = reviewService.getReviewsForExpert(token);

        log.info("Successfully fetched {} reviews for expert with token", reviews.size());
        return ResponseEntity.ok(reviews);
    }
}