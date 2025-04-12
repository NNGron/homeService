package ir.service.expert;

import ir.convert.offer.OfferEntityToOfferResponseDtoConvertor;
import ir.convert.order.order_response.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Expert;
import ir.entity.Offer;
import ir.entity.Order;
import ir.entity.User;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.ExpertRepository;
import ir.security.JwtTokenProvider;
import ir.service.offer.OfferService;
import ir.service.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ir.entity.enums.OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER;

/**
 * Service implementation for managing expert offers.
 *
 * <p>This service provides functionality for experts to create, update, and delete their offers
 * while ensuring proper authentication and validation of user status.</p>
 */
@Service
@Slf4j
@AllArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final ExpertRepository expertRepository;
    private final OrderService orderService;
    private final OfferService offerService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    @Override
    public OfferResponseDto saveOffer(OfferRequestDto offerRequestDto,
                                      String token) {

        Order order = orderService.findById(offerRequestDto.getOrderNumber());
        log.info("Found order with ID: {}", offerRequestDto.getOrderNumber());

        String username = jwtTokenProvider.extractUsername(token);
        Optional<Expert> optionalUser = expertRepository.findByUserName(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;
            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user.getStatus().equals(UserStatus.DELETED)) {
                throw new CustomException("User already deleted", ErrorCode.NOT_FOUND.getCode());
            }
            // Check if the expert has already made an offer for this order
            boolean hasExistingOffer = order.getOffers().stream()
                    .anyMatch(offer -> offer.getExpert().getId().equals(expert.getId()));
            if (hasExistingOffer) {
                throw new CustomException(
                        "Expert has already made an offer for orderNumber " + offerRequestDto.getOrderNumber(),
                        ErrorCode.DUPLICATE_OFFER.getCode());
            }
            log.info("Creating new offer for order with ID: {}", offerRequestDto.getOrderNumber());
            Offer offer = new Offer();
            offer.setExpert(expert);
            offer.setProposedPrice(offerRequestDto.getProposedPrice());
            offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
            offer.setAvailableTime(offerRequestDto.getAvailableTime());
            order.setStatus(WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);

            order = orderService.save(order);
            offer.setOrder(order);
            offer = offerService.saveOffer(offer);

            return offerEntityToOfferResponseDtoConvertor.apply(offer);
        }
        throw new CustomException("Expert not find", ErrorCode.NOT_FOUND.getCode());
    }

    @Override
    public OfferResponseDto updateOffer(Long offerNumber, OfferRequestDto offerRequestDto, String token) {
        log.info("Attempting to update offer with ID: {} for expert using token.", offerNumber);

        // Step 1: Extract username from token and validate expert
        String username = jwtTokenProvider.extractUsername(token);
        Expert expert = expertRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("Expert with username {} not found.", username);
                    return new CustomException("Expert not found.", ErrorCode.NOT_FOUND.getCode());
                });

        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with username {} is not approved to update offers.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 2: Validate the offer
        Offer offer = offerService.findOfferById(offerNumber);
        if (offer == null) {
            log.error("Offer not found with ID: {}", offerNumber);
            throw new CustomException(
                    "Offer not found with offer ID: " + offerNumber, ErrorCode.NOT_FOUND.getCode());
        }

        // Step 3: Ensure the offer belongs to the expert
        if (!offer.getExpert().equals(expert)) {
            log.error("Offer with ID: {} does not belong to expert with username: {}", offerNumber, username);
            throw new CustomException(
                    "This offer does not belong to the expert.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 4: Validate the offer's order status
        if (offer.getOrder() != null && offer.getOrder().getStatus() != OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER) {
            log.error("Cannot update offer with ID: {} as it is already used in an order.", offerNumber);
            throw new CustomException("You cannot update this offer because it has already been used in an order.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        // Step 5: Validate the order
        Order order = orderService.findById(offerRequestDto.getOrderNumber());
        if (order == null) {
            log.error("Order not found with ID: {}", offerRequestDto.getOrderNumber());
            throw new CustomException(
                    "Order not found with order ID: " + offerRequestDto.getOrderNumber(), ErrorCode.NOT_FOUND.getCode());
        }

        // Step 6: Update offer details
        log.info("Updating offer with ID: {}", offerNumber);
        offer.setProposedPrice(offerRequestDto.getProposedPrice());
        offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
        offer.setAvailableTime(offerRequestDto.getAvailableTime());

        // Update order status
        order.setStatus(OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);
        order = orderService.save(order);

        // Link the updated order to the offer and save
        offer.setOrder(order);
        offer = offerService.saveOffer(offer);

        log.info("Offer with ID: {} updated successfully for expert with username: {}", offerNumber, username);
        return offerEntityToOfferResponseDtoConvertor.apply(offer);
    }

    @Override
    public void deleteOffer(Long offerId, String token) {
        log.info("Attempting to delete offer with ID: {} for expert using token.", offerId);

        // Step 1: Extract username from token and validate expert
        String username = jwtTokenProvider.extractUsername(token);
        Expert expert = expertRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("Expert with username {} not found.", username);
                    return new CustomException("Expert not found.", ErrorCode.NOT_FOUND.getCode());
                });

        // Step 2: Validate expert status
        if (expert.getStatus().equals(UserStatus.NEW)) {
            log.error("Expert with username {} is not approved to delete offers.", username);
            throw new CustomException("User should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        // Step 3: Validate the offer
        Offer offer = offerService.findOfferById(offerId);
        if (offer == null) {
            log.error("Offer not found with ID: {}", offerId);
            throw new CustomException("Offer not found with offer ID: " + offerId, ErrorCode.NOT_FOUND.getCode());
        }

        // Step 4: Check order status associated with the offer
        if (offer.getOrder() != null && offer.getOrder().getStatus() != OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER) {
            log.error("Cannot delete offer with ID: {} as it is already used in an order.", offerId);
            throw new CustomException("You cannot delete this offer because it has already been used in an order.",
                    ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        }

        // Step 5: Ensure the offer belongs to the expert
        if (!offer.getExpert().equals(expert)) {
            log.error("Offer with ID: {} does not belong to expert with username: {}", offerId, username);
            throw new CustomException("This offer does not belong to the expert.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 6: Delete the offer
        log.info("Deleting offer with ID: {}.", offerId);
        offerService.deleteOffer(offer);
        log.info("Offer with ID: {} deleted successfully for expert with username: {}", offerId, username);
    }
}