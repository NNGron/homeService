package ir.service.order;

import ir.convert.order.OrderResponse.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.*;
import ir.entity.enums.OrderStatus;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.OrderRepository;
import ir.service.category.CategoryService;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderEntityToOrderResponseDtoConvertor orderEntityToOrderResponseDtoConvertor;

    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderDto, String email, String password) {
        User user = userService.findByEmailAndPassword(email, password);
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDescription(orderDto.getOrderDescription());
            order.setOrderDate(orderDto.getOrderDate());
            order.setOrderAddress(orderDto.getOrderAddress());
            order.setStatus(OrderStatus.WAITING_FOR_EXPERT_PROPOSALS);
            order.setExecutionDate(orderDto.getExecutionDate());

            String subCategoryName = orderDto.getSubCategoryName();
            Optional<Category> optionalCategory = categoryService.findSubCategoryByName(subCategoryName);
            if(optionalCategory.isEmpty()) {
                throw new CustomException("Category not found with provided name", ErrorCode.NOT_FOUND.getCode());
            }
            order.setCategory(optionalCategory.get());
            order = orderRepository.save(order);
            return orderEntityToOrderResponseDtoConvertor.apply(order);
        }
        throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
    }

    @Override
    public List<Order> findOrdersByCategories(List<Category> categories) {
        Optional<List<Order>> optionalOrderList = orderRepository.findAllByCategoryIn(categories);
        return optionalOrderList.orElse(new ArrayList<>());
    }

    @Override
    public List<OrderResponseDto> getOrder(String email, String password) {
        User user = userService.findByEmailAndPassword(email, password);
        if (user != null) {
            Expert expert = (Expert) user;
            if (user.getStatus().equals(UserStatus.NEW)) {
                throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
            }
            List<Category> categories = expert.getCategories();
            List<Order> ordersByCategories = findOrdersByCategories(categories);
            List<OrderResponseDto> orderResponseDtos = orderEntityToOrderResponseDtoConvertor.convertList(ordersByCategories);
            return orderResponseDtos;
        }
        throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
    }

    @Override
    public Order findById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder
                .orElseThrow(() -> new CustomException("Order not found with provided order number", ErrorCode.NOT_FOUND.getCode()));

    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(String email, String password,
                                              OrderStatus orderStatus,
                                              Long orderNumber, Long offerNumber) {
        if (OrderStatus.PAID.equals(orderStatus)) {
            throw new CustomException("Order status should not be paid", ErrorCode.ORDER_STATUS_SHOULD_NOT_BE_PAID.getCode());
        }
        User user = userService.findByEmailAndPassword(email, password);
        if (user == null) {
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }
        Order order = findById(orderNumber);
        if (order == null) {
            throw new CustomException("Order not found with provided order number", ErrorCode.NOT_FOUND.getCode());
        }

        if (OrderStatus.WAITING_FOR_EXPERT_ARRIVAL.equals(orderStatus)) {
            Optional<Offer> offerOptional = order.getOffers().stream().filter(offer -> offer.getId().equals(offerNumber)).findFirst();
            if (offerOptional.isPresent()) {
                Offer offer = offerOptional.get();
                Double proposedPrice = offer.getProposedPrice();
                order.setFinalPrice(proposedPrice);
            }
        }
        order.setStatus(orderStatus);
        orderRepository.save(order);
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }

    @Override
    public OrderResponseDto paid(String email,
                                 String password,
                                 Long orderNumber,
                                 Long offerNumber) {

        User user = userService.findByEmailAndPassword(email, password);
        if (user == null || !(user instanceof Customer)) {
            throw new CustomException("User not found with provided email and password", ErrorCode.NOT_FOUND.getCode());
        }
        Order order = findById(orderNumber);
        if (order == null) {
            throw new CustomException("Order not found with provided order number", ErrorCode.NOT_FOUND.getCode());
        }
        Optional<Offer> offerOptional = order.getOffers().stream().filter(offer -> offer.getId().equals(offerNumber)).findFirst();
        if (offerOptional.isPresent()) {
            Offer offer = offerOptional.get();
            Expert expert = offer.getExpert();
            Double finalPrice = order.getFinalPrice();
            Customer customer = (Customer) user;
            if (customer.getCredit() == null || customer.getCredit() < finalPrice) {
                throw new CustomException("Credit is not enough", ErrorCode.CREDIT_NOT_ENOUGH.getCode());
            }
            Double expertCredit = expert.getCredit() != null ? expert.getCredit() : 0;
            customer.setCredit(customer.getCredit() - finalPrice);
            expert.setCredit(expertCredit + finalPrice);
            userService.saveUser(customer);
            userService.saveUser(expert);
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderEntityToOrderResponseDtoConvertor.apply(order);
    }
}
