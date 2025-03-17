package ir.service.order;

import ir.convert.order.OrderResponse.OrderEntityToOrderResponseDtoConvertor;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.*;
import ir.entity.enums.OrderStatus;
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
    public List<Order> findOrdersByCategories(List<Category> categories, OrderStatus orderStatus) {
        Optional<List<Order>> optionalOrderList = orderRepository.findAllByCategoryInAndStatus(categories, orderStatus);
        return optionalOrderList.orElse(new ArrayList<>());
    }

    @Override
    public List<OrderResponseDto> getOrder(String email, String password) {
        User user = userService.findByEmailAndPassword(email, password);
        if (user != null) {
            Expert expert = (Expert) user;
            List<Category> categories = expert.getCategories();
            List<Order> ordersByCategories = findOrdersByCategories(categories, OrderStatus.WAITING_FOR_EXPERT_PROPOSALS);
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

}
