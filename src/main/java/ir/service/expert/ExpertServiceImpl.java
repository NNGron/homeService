package ir.service.expert;

import ir.convert.offer.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Expert;
import ir.entity.Offer;
import ir.entity.Order;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.ExpertRepository;
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
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    /**
     * Saves a new offer submitted by the expert.
     *
     * @param offerRequestDto the details of the offer to be saved
     * @param email           the expert's email for authentication
     * @param password        the expert's password for authentication
     * @return the saved offer as a {@link OfferResponseDto}
     * @throws CustomException if the expert is not found, not approved, or deleted
     */
    @Override
    public OfferResponseDto saveOffer(OfferRequestDto offerRequestDto,
                                      String email,
                                      String password) {
        log.info("Attempting to save a new offer for expert with email: {}", email);

        Order order = orderService.findById(offerRequestDto.getOrderNumber());
        log.info("Found order with ID: {}", offerRequestDto.getOrderNumber());

        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;
            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("Expert with email {} is not approved to create offers.", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            if (user.getStatus().equals(UserStatus.DELETED)) {
                log.error("Expert with email {} has been deleted.", email);
                throw new CustomException("User already deleted", ErrorCode.NOT_FOUND.getCode());
            }
            // Check if the expert has already made an offer for this order
            boolean hasExistingOffer = order.getOffers().stream()
                    .anyMatch(offer -> offer.getExpert().getId().equals(expert.getId()));
            if (hasExistingOffer) {
                log.warn("Expert with email {} has already made an offer for orderNumber {}",
                        email, offerRequestDto.getOrderNumber());
                throw new CustomException(
                        "Expert has already made an offer for orderNumber " + offerRequestDto.getOrderNumber(),
                        ErrorCode.DUPLICATE_OFFER.getCode());
            }
            log.info("Creating new offer for order with ID: {}", offerRequestDto.getOrderNumber());
            Offer offer = new Offer();
            offer.setExpert(expert);
            offer.setProposedPrice(offerRequestDto.getProposedPrice());
            offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
            offer.setStartTime(offerRequestDto.getStartTime());
            order.setStatus(WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);

            order = orderService.save(order);
            offer.setOrder(order);
            offer = offerService.saveOffer(offer);

            log.info("Offer created successfully for expert with email: {}", email);
            return offerEntityToOfferResponseDtoConvertor.apply(offer);
        }
        log.error("Expert not found with email: {}", email);
        throw new CustomException("Expert not find", ErrorCode.NOT_FOUND.getCode());
    }

    /**
     * Updates an existing offer submitted by the expert.
     *
     * @param offerNumber     the unique number of the offer to be updated
     * @param offerRequestDto the updated details of the offer
     * @param email           the expert's email for authentication
     * @param password        the expert's password for authentication
     * @return the updated offer as a {@link OfferResponseDto}
     * @throws CustomException if the expert is not found, not approved, or unauthorized to update the offer
     */
    @Override
    public OfferResponseDto updateOffer(Long offerNumber,
                                        OfferRequestDto offerRequestDto,
                                        String email,
                                        String password) {
        log.info("Attempting to update offer with ID: {} for expert with email: {}", offerNumber, email);
        Order order = orderService.findById(offerRequestDto.getOrderNumber());

        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;

            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("Expert with email {} is not approved to update offers.", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }

            Offer offer = offerService.findOfferById(offerNumber);
            if (offer == null) {
                log.error("Offer not found with ID: {}", offerNumber);
                throw new CustomException(
                        "Offer not found with offer ID: " + offerNumber, ErrorCode.NOT_FOUND.getCode());
            }

            if (offer.getOrder() != null && offer.getOrder().getStatus() != WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER) {
                log.error("Cannot update offer with ID: {} as it is already used in an order.", offerNumber);
                throw new CustomException("You cannot update this offer because it has already been used in an order",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
            if (!offer.getExpert().equals(expert)) {
                log.error("Offer with ID: {} does not belong to expert with email: {}", offerNumber, email);
                throw new CustomException(
                        "This offer does not belong to the expert", ErrorCode.UNAUTHORIZED.getCode());
            }
            log.info("Updating offer with ID: {}", offerNumber);
            offer.setProposedPrice(offerRequestDto.getProposedPrice());
            offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
            offer.setStartTime(offerRequestDto.getStartTime());

            order.setStatus(WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);
            order = orderService.save(order);

            offer.setOrder(order);
            offer = offerService.saveOffer(offer);

            log.info("Offer with ID: {} updated successfully for expert with email: {}", offerNumber, email);
            return offerEntityToOfferResponseDtoConvertor.apply(offer);
        }
        log.error("Expert not found with email: {}", email);
        throw new CustomException("Expert not found", ErrorCode.NOT_FOUND.getCode());
    }

    /**
     * Deletes an offer submitted by the expert.
     *
     * @param offerId the unique ID of the offer to be deleted
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     * @throws CustomException if the expert is not found, unauthorized, or the offer is already used in an order
     */
    @Override
    public void deleteOffer(Long offerId,
                            String email,
                            String password) {
        log.info("Attempting to delete offer with ID: {} for expert with email: {}", offerId, email);

        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;

            if (user.getStatus().equals(UserStatus.NEW)) {
                log.error("Expert with email {} is not approved to delete offers.", email);
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            Offer offer = offerService.findOfferById(offerId);
            if (offer == null) {
                log.error("Offer not found with ID: {}", offerId);
                throw new CustomException("Offer not found with offer ID: " + offerId, ErrorCode.NOT_FOUND.getCode());
            }
            if (offer.getOrder() != null && offer.getOrder().getStatus() != WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER) {
                log.error("Cannot delete offer with ID: {} as it is already used in an order.", offerId);
                throw new CustomException("You cannot delete this offer because it has already been used in an order",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
            if (!offer.getExpert().equals(expert)) {
                log.error("Offer with ID: {} does not belong to expert with email: {}", offerId, email);
                throw new CustomException("This offer does not belong to the expert", ErrorCode.UNAUTHORIZED.getCode());
            }
            offerService.deleteOffer(offer);
            log.info("Offer with ID: {} deleted successfully for expert with email: {}", offerId, email);
        } else {
            log.error("Expert not found with email: {}", email);
            throw new CustomException("Expert not found", ErrorCode.NOT_FOUND.getCode());
        }
    }
}