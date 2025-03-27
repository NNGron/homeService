package ir.service.review;

import ir.convert.review.ReviewEntityToReviewResponseDtoConvertor;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.*;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.ReviewRepository;
import ir.service.order.OrderService;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final OrderService orderService;
    private final ReviewEntityToReviewResponseDtoConvertor reviewEntityToReviewResponseDtoConvertor;

    @Override
    public ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto) {
        Optional<User> optionalUser = Optional.ofNullable(userService.findByEmailAndPassword(email, password));
        return optionalUser
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
                .orElseThrow(() -> new CustomException(
                        "User not found or is not a customer/ order-number or offer-number not found",
                        ErrorCode.NOT_FOUND.getCode()));
    }

    private Review createAndSaveReview(
            Customer customer, Offer offer, Order order, ReviewRequestDto reviewRequestDto) {
        Expert expert = offer.getExpert();
        Review review = new Review();
        review.setCustomer(customer);
        review.setExpert(expert);
        review.setOrder(order);
        review.setComment(reviewRequestDto.comment());
        review.setRating(reviewRequestDto.rating());


        List<Integer> allRates = new ArrayList<>(findAllReviewsByExpertId(expert.getId()).stream()
                .map(Review::getRating).toList());
        allRates.add(reviewRequestDto.rating());
        double averageRate = allRates.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
        expert.setRating(averageRate);
        userService.saveUser(expert);
        try {
            return reviewRepository.save(review);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException("This order already has a comment", ErrorCode.ALREADY_EXIST.getCode());
        }
    }

    @Override
    public List<Review> findAllReviewsByExpertId(Long expertId) {
        return reviewRepository.findAllByExpert_Id(expertId);
    }


    @Override
    public List<ReviewResponseDto> getReviewsForExpert(String email, String password) {
        User user = userService.findByEmailAndPassword(email, password);
        if (!(user instanceof Expert expert)) {
            throw new CustomException("User not found or is not an expert", ErrorCode.NOT_FOUND.getCode());
        }

        if (expert.getStatus().equals(UserStatus.NEW)) {
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        List<Review> reviews = reviewRepository.findAllByExpert_Id(expert.getId());

        return reviews.stream()
                .map(reviewEntityToReviewResponseDtoConvertor)
                .toList();
    }

}


// @Override
//    public ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto) {
//        return Optional.ofNullable(userService.findByEmailAndPassword(email, password))
//                .filter(user -> user instanceof Customer)
//                .map(user -> (Customer) user)
//                .flatMap(customer -> customer.getOrders().stream()
//                        .filter(order -> order.getId().equals(reviewRequestDto.orderNumber()))
//                        .findFirst()
//                        .flatMap(order -> order.getOffers().stream()
//                                .filter(offer -> offer.getId().equals(reviewRequestDto.offerNumber()))
//                                .findFirst()
//                                .map(offer -> createAndSaveReview(customer, offer, order, reviewRequestDto))))
//                .map(reviewEntityToReviewResponseDtoConvertor)
//                .orElseThrow(() -> new CustomException("User not found or is not a customer", ErrorCode.NOT_FOUND.getCode()));
//    }