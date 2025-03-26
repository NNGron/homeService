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

import static ir.entity.enums.OrderStatus.WAITING_FOR_EXPERT_PROPOSALS;
import static ir.entity.enums.OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER;

@Service
@Slf4j
@AllArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final ExpertRepository expertRepository;
    private final OrderService orderService;
    private final OfferService offerService;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    @Override
    public OfferResponseDto saveOffer(OfferRequestDto offerRequestDto,
                                      String email,
                                      String password) {
        Order order = orderService.findById(offerRequestDto.getOrderNumber());

        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;
            if (user.getStatus().equals(UserStatus.NEW)){
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            Offer offer = new Offer();
            offer.setExpert(expert);
            offer.setProposedPrice(offerRequestDto.getProposedPrice());
            offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
            offer.setStartTime(offerRequestDto.getStartTime());
            order.setStatus(WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);
            order = orderService.save(order);
            offer.setOrder(order);
            offer = offerService.saveOffer(offer);
            OfferResponseDto offerResponseDto = offerEntityToOfferResponseDtoConvertor.apply(offer);
            return offerResponseDto;
        }
        throw new CustomException("Expert not find", ErrorCode.NOT_FOUND.getCode());
    }

    @Override
    public OfferResponseDto updateOffer(Long offerNumber,
                                        OfferRequestDto offerRequestDto,
                                        String email,
                                        String password) {
        Order order = orderService.findById(offerRequestDto.getOrderNumber());

        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;

            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }

            Offer offer = offerService.findOfferById(offerNumber);
            if (offer == null) {
                throw new CustomException("Offer not found with offer ID: " + offerNumber, ErrorCode.NOT_FOUND.getCode());
            }

            if (offer.getOrder() != null && offer.getOrder().getStatus() != WAITING_FOR_EXPERT_PROPOSALS) {
                throw new CustomException("You cannot update this offer because it has already been used in an order",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
            if (!offer.getExpert().equals(expert)) {
                throw new CustomException("This offer does not belong to the expert", ErrorCode.UNAUTHORIZED.getCode());
            }

            offer.setProposedPrice(offerRequestDto.getProposedPrice());
            offer.setEstimatedTime(offerRequestDto.getEstimatedTime());
            offer.setStartTime(offerRequestDto.getStartTime());

            order.setStatus(WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER);
            order = orderService.save(order);

            offer.setOrder(order);
            offer = offerService.saveOffer(offer);

            return offerEntityToOfferResponseDtoConvertor.apply(offer);
        }
        throw new CustomException("Expert not found", ErrorCode.NOT_FOUND.getCode());
    }

    @Override
    public void deleteOffer(Long offerId,
                            String email,
                            String password) {
        Optional<Expert> optionalUser = expertRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Expert expert = (Expert) user;

            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            Offer offer = offerService.findOfferById(offerId);
            if (offer == null) {
                throw new CustomException("Offer not found with offer ID: " + offerId, ErrorCode.NOT_FOUND.getCode());
            }
            if (offer.getOrder() != null && offer.getOrder().getStatus() != WAITING_FOR_EXPERT_PROPOSALS) {
                throw new CustomException("You cannot delete this offer because it has already been used in an order",
                        ErrorCode.UNPROCESSABLE_ENTITY.getCode());
            }
            if (!offer.getExpert().equals(expert)) {
                throw new CustomException("This offer does not belong to the expert", ErrorCode.UNAUTHORIZED.getCode());
            }
            offerService.deleteOffer(offer);
        } else {
            throw new CustomException("Expert not found", ErrorCode.NOT_FOUND.getCode());
        }
    }

}
