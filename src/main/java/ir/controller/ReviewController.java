package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.service.review.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/comment")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<ReviewResponseDto> saveComment(@RequestParam String email,
                                                         @RequestParam String password,
                                                         @RequestBody ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.addReview(email, password, reviewRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @GetMapping("/expert-comments")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForExpert(@RequestParam String email,
                                                                       @RequestParam String password) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsForExpert(email, password);
        return ResponseEntity.ok(reviews);
    }
}
