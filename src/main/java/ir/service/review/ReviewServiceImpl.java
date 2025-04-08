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
import ir.security.JwtTokenProvider;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewEntityToReviewResponseDtoConvertor reviewEntityToReviewResponseDtoConvertor;

@Override
public ReviewResponseDto addReview(String token, ReviewRequestDto reviewRequestDto) {
    log.info("Adding review for customer with token.");

    // Step 1: Extract username from token and validate user
    String username = jwtTokenProvider.extractUsername(token);
    Optional<User> userOptional = userService.findByUserName(username);

    if (userOptional.isEmpty() || !(userOptional.get() instanceof Customer)) {
        log.error("User not found or is not a customer. Username: {}", username);
        throw new CustomException("User not found or not a customer.", ErrorCode.NOT_FOUND.getCode());
    }

    Customer customer = (Customer) userOptional.get();

    // Step 2: Validate associated order
    Optional<Order> orderOptional = customer.getOrders().stream()
            .filter(order -> order.getId().equals(reviewRequestDto.orderNumber()))
            .findFirst();
    if (orderOptional.isEmpty()) {
        log.error("Order with orderNumber {} not found for customer.", reviewRequestDto.orderNumber());
        throw new CustomException("Order not found with provided order number.", ErrorCode.NOT_FOUND.getCode());
    }

    Order order = orderOptional.get();

    // Step 3: Validate associated offer
    Optional<Offer> offerOptional = order.getOffers().stream()
            .filter(offer -> offer.getId().equals(reviewRequestDto.offerNumber()))
            .findFirst();
    if (offerOptional.isEmpty()) {
        log.error("Offer with offerNumber {} not found in order.", reviewRequestDto.offerNumber());
        throw new CustomException("Offer not found with provided offer number.", ErrorCode.NOT_FOUND.getCode());
    }

    Offer offer = offerOptional.get();

    // Step 4: Create and save the review
    Review review = createAndSaveReview(customer, offer, order, reviewRequestDto);

    log.info("Review successfully added for orderNumber {} and offerNumber {}.",
            reviewRequestDto.orderNumber(), reviewRequestDto.offerNumber());
    return reviewEntityToReviewResponseDtoConvertor.apply(review);
}

    private Review createAndSaveReview(Customer customer,
                                       Offer offer,
                                       Order order,
                                       ReviewRequestDto reviewRequestDto) {
        Expert expert = offer.getExpert();

        // Prepare review entity
        Review review = new Review();
        review.setCustomer(customer);
        review.setExpert(expert);
        review.setOrder(order);
        review.setComment(reviewRequestDto.comment());
        review.setRating(reviewRequestDto.rating());

        // Calculate and update expert's average rating
        List<Integer> allRatings = findAllReviewsByExpertId(expert.getId()).stream()
                .map(Review::getRating)
                .collect(Collectors.toList());
        allRatings.add(reviewRequestDto.rating());

        double averageRating = allRatings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
        expert.setRating(averageRating);

        // Save expert and review
        userService.saveUser(expert);
        try {
            return reviewRepository.save(review);
        } catch (DataIntegrityViolationException ex) {
            log.error("Review creation failed. This order already has a review.");
            throw new CustomException("This order already has a comment.", ErrorCode.ALREADY_EXIST.getCode());
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

    @Override
    public List<ExpertReviewResponseDto> getReviewsForExpert(String token) {
        log.info("Fetching reviews for expert with token");

        // Extract username from token and find user
        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isEmpty()) {
            log.error("User not found. Username: {}", username);
            throw new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
        }

        return getReviewsForExpert((Expert) validateExpert(userOptional.get(), username));
    }

    // Reusable method to fetch and map reviews for an expert
    private List<ExpertReviewResponseDto> getReviewsForExpert(Expert expert) {
        List<Review> reviews = reviewRepository.findAllByExpert_Id(expert.getId());
        log.info("Successfully fetched {} reviews for expert with ID: {}", reviews.size(), expert.getId());

        return reviews.stream()
                .map(review -> new ExpertReviewResponseDto(
                        review.getRating(), review.getOrder().getCategory().getName()))
                .toList();
    }

    // Validates if the user is an approved expert
    private User validateExpert(User user, String identifier) {
        if (!(user instanceof Expert expert)) {
            log.error("User is not an expert. Identifier: {}", identifier);
            throw new CustomException("User not found or is not an expert.", ErrorCode.NOT_FOUND.getCode());
        }

        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert is not approved. Identifier: {}", identifier);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }
        return expert;
    }
}