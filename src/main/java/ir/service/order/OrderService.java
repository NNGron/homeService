package ir.service.order;

import ir.dto.bankcard.BankCardInfoDto;
import ir.dto.filter.ComprehensiveFilterDto;
import ir.dto.filter.ComprehensiveResponseDto;
import ir.dto.filter.FilterOrderByWorkingDateDto;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Category;
import ir.entity.Order;
import ir.entity.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing orders.
 *
 * <p>This interface provides methods for creating, retrieving, updating,
 * deleting, and managing the payment and status of orders.</p>
 */
public interface OrderService {


    OrderResponseDto saveOrder(OrderRequestDto orderDto,
                               String token);

    List<OrderResponseDto> getOrdersByCustomerEmail(String token);

    List<Order> findOrdersByCategories(List<Category> categories);

    List<OrderResponseDto> getOrder(String token);

    Order findById(Long id);

    Order save(Order order);

    OrderResponseDto updateOrderStatus(String token,
                                       OrderStatus orderStatus,
                                       Long orderNumber, Long offerNumber);

    OrderResponseDto paid(String token,
                          Long orderNumber,
                          Long offerNumber);

    OrderResponseDto updateOrder(Long orderNumber,
                                 OrderRequestDto orderDto,
                                 String token);

    void deleteOrder(Long orderId, String token);

    String payOnlineByCard(String token, BankCardInfoDto bankCardInfoDto, Long orderNumber, Long offerNumber);

    Map<String, Long> getOrderReport();

    List<ComprehensiveResponseDto> filterComprehensiveOrders(ComprehensiveFilterDto filterDto, Pageable pageable);

    List<OrderResponseDto> getOrdersByWorkingDate(FilterOrderByWorkingDateDto filterOrderByWorkingDateDto);
}