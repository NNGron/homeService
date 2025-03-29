package ir.service.review;

import ir.convert.review.ReviewEntityToReviewResponseDtoConvertor;
import ir.dto.expert.ExpertReviewResponseDto;
import ir.dto.review.ReviewRequestDto;
import ir.dto.review.ReviewResponseDto;
import ir.entity.*;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.ReviewRepository;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing reviews.
 *
 * <p>This class provides functionality to add new reviews, fetch reviews for experts,
 * and find reviews by expert ID. It includes validation for authentication and user roles.</p>
 */
@Service
@AllArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ReviewEntityToReviewResponseDtoConvertor reviewEntityToReviewResponseDtoConvertor;

    /**
     * Adds a new review for the authenticated customer.
     *
     * <p>Validates the customer's authentication and ensures the specified order
     * and offer exist before creating the review.</p>
     *
     * @param email            the customer's email for authentication
     * @param password         the customer's password for authentication
     * @param reviewRequestDto the details of the review to be added
     * @return the added review as a {@link ReviewResponseDto}
     * @throws CustomException if validation fails or the review already exists
     */
    @Override
    public ReviewResponseDto addReview(String email, String password, ReviewRequestDto reviewRequestDto) {
        log.info("Starting to add review for customer with email: {}", email);

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

    /**
     * Creates and saves a review for the specified customer, order, and offer.
     *
     * <p>Calculates the average rating for the expert based on all reviews.</p>
     *
     * @param customer the customer adding the review
     * @param offer the offer linked to the review
     * @param order the order linked to the review
     * @param reviewRequestDto the details of the review to be created
     * @return the saved {@link Review} entity
     * @throws CustomException if the review already exists
     */
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

    /**
     * Finds all reviews for a specific expert based on their ID.
     *
     * @param expertId the unique ID of the expert
     * @return a list of {@link Review} entities for the expert
     */
    @Override
    public List<Review> findAllReviewsByExpertId(Long expertId) {
        log.info("Fetching reviews for expert with ID: {}", expertId);
        return reviewRepository.findAllByExpert_Id(expertId);
    }

    /**
     * Retrieves a list of reviews for an authenticated expert.
     *
     * <p>This method fetches all reviews associated with the authenticated expert. It ensures
     * that the user is an expert and is approved before proceeding. The returned reviews
     * only include the rating and the subcategory name.</p>
     *
     * @param email    the expert's email for authentication
     * @param password the expert's password for authentication
     * @return a list of {@link ExpertReviewResponseDto} containing the rating and subcategory name of each review
     * @throws CustomException if the user is not an expert, not approved, or not found
     */
    @Override
    public List<ExpertReviewResponseDto> getReviewsForExpert(String email, String password) {
        log.info("Fetching reviews for expert with email: {}", email);

        User user = userService.findByEmailAndPassword(email, password);
        if (!(user instanceof Expert expert)) {
            log.error("User is not an expert or not found. Email: {}", email);
            throw new CustomException("User not found or is not an expert", ErrorCode.NOT_FOUND.getCode());
        }

        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with email {} is not approved", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        List<Review> reviews = reviewRepository.findAllByExpert_Id(expert.getId());
        log.info("Successfully fetched {} reviews for expert with email: {}", reviews.size(), email);

        return reviews.stream()
                .map(review -> new ExpertReviewResponseDto(
                        review.getRating(), review.getOrder().getCategory().getName()))
                .toList();
    }
}