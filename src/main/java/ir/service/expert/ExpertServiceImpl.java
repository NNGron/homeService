package ir.service.expert;

import ir.convert.offer.offerResponse.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Expert;
import ir.entity.Offer;
import ir.entity.Order;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.user.UserRepository;
import ir.service.offer.OfferService;
import ir.service.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ir.entity.enums.OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER;

@Service
@Slf4j
@AllArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OfferService offerService;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    @Override
    public OfferResponseDto saveOffer(OfferRequestDto offerRequestDto, String email, String password) {
        Order order = orderService.findById(offerRequestDto.getOrderNumber());

        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, password);
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
}
