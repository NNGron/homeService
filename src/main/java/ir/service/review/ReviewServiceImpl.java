package ir.service.review;

import ir.convert.review.reviewResponse.ReviewEntityToReviewResponseDtoConvertor;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.*;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.ReviewRepository;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ReviewEntityToReviewResponseDtoConvertor reviewEntityToReviewResponseDtoConvertor;

    @Override
    public ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto) {
        return Optional.ofNullable(userService.findByEmailAndPassword(email, password))
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .flatMap(customer -> customer.getOrders().stream()
                        .filter(order -> order.getId().equals(reviewRequestDto.orderNumber()))
                        .findFirst()
                        .flatMap(order -> order.getOffers().stream()
                                .filter(offer -> offer.getId().equals(reviewRequestDto.offerNumber()))
                                .findFirst()
                                .map(offer -> createAndSaveReview(customer, offer, order, reviewRequestDto))))
                .map(reviewEntityToReviewResponseDtoConvertor::apply)
                .orElseThrow(() -> new CustomException("User not found or is not a customer", ErrorCode.NOT_FOUND.getCode()));
    }

    private Review createAndSaveReview(Customer customer, Offer offer, Order order, ReviewRequestDto reviewRequestDto) {
        Review review = new Review();
        review.setCustomer(customer);
        review.setExpert(offer.getExpert());
        review.setOrder(order);
        review.setComment(reviewRequestDto.comment());
        review.setRating(reviewRequestDto.rating());
        return reviewRepository.save(review);
    }
}
