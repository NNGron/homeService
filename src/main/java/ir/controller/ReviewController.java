package ir.controller;

import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.service.review.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/comment")
    public ResponseEntity<ReviewResponseDto> saveComment(@RequestParam String email,
                                                         @RequestParam String password,
                                                         @RequestBody ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.addReview(email, password, reviewRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }
}
