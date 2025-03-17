package ir.service.customer;

import ir.convert.offer.offerResponse.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Customer;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    @Override
    public List<OfferResponseDto> getCustomerOffers(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .map(customer -> customer.getOrders().stream()
//                        .filter(order -> order.getStatus() == OrderStatus.WAITING_FOR_EXPERT_SELECTION_BY_CUSTOMER)
                        .flatMap(order -> order.getOffers().stream())
                        .collect(Collectors.toList()))
                .map(offerEntityToOfferResponseDtoConvertor::convertList)
                .orElseThrow(() -> new CustomException("User not found", ErrorCode.NOT_FOUND.getCode()));
    }
}
