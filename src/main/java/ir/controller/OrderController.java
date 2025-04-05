package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.enums.OrderStatus;
import ir.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> saveCustomerOrder(@Valid @RequestBody OrderRequestDto orderDto,
                                                              @RequestParam String email,
                                                              @RequestParam String password) {
        log.info("Received request to create an order for customer with email: {}", email);
        log.debug("Order details: {}", orderDto);

        OrderResponseDto orderResponseDto = orderService.saveOrder(orderDto, email, password);

        log.info("Successfully created order for customer with email: {}", email);
        return ResponseEntity.ok(orderResponseDto);
    }


    @PutMapping("/update-status")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> updateStatus(@RequestParam String email,
                                                         @RequestParam String password,
                                                         @RequestParam OrderStatus status,
                                                         @RequestParam Long orderNumber,
                                                         @RequestParam Long offerNumber) {
        log.info("Received request to update order status for customer with email: {}", email);
        log.debug("Status: {}, OrderNumber: {}, OfferNumber: {}", status, orderNumber, offerNumber);

        OrderResponseDto orderResponseDto =
                orderService.updateOrderStatus(email, password, status, orderNumber, offerNumber);

        log.info("Order status successfully updated for order number: {} by customer with email: {}",
                orderNumber, email);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/show-orders")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<List<OrderResponseDto>> getAllUsers(@RequestParam String email,
                                                              @RequestParam String password) {
        log.info("Fetching all orders for expert with email: {}", email);

        List<OrderResponseDto> orders = orderService.getOrder(email, password);

        log.info("Successfully retrieved {} orders for expert with email: {}", orders.size(), email);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders_history")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrders(@RequestParam String email,
                                                                    @RequestParam String password) {
        log.info("Fetching order history for customer with email: {}", email);

        List<OrderResponseDto> orders = orderService.getOrdersByCustomerEmail(email, password);

        log.info("Successfully retrieved order history with {} orders for customer with email: {}",
                orders.size(), email);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/paid")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> paid(@RequestParam String email,
                                                 @RequestParam String password,
                                                 @RequestParam Long orderNumber,
                                                 @RequestParam Long offerNumber) {
        log.info("Processing payment for order number: {} by customer with email: {}", orderNumber, email);

        OrderResponseDto orderResponseDto = orderService.paid(email, password, orderNumber, offerNumber);

        log.info("Payment successfully processed for order number: {} by customer with email: {}", orderNumber, email);
        return ResponseEntity.ok(orderResponseDto);
    }

    @PutMapping("/update-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> updateCustomerOrder(@Valid @RequestBody OrderRequestDto orderDto,
                                                                @RequestParam String email,
                                                                @RequestParam String password,
                                                                @RequestParam Long orderId) {
        log.info("Received request to update order with ID: {} by customer with email: {}", orderId, email);
        log.debug("Updated order details: {}", orderDto);

        OrderResponseDto updatedOrder = orderService.updateOrder(orderId, orderDto, email, password);

        log.info("Order with ID: {} successfully updated by customer with email: {}", orderId, email);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/delete-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<String> deleteCustomerOrder(@RequestParam String email,
                                                      @RequestParam String password,
                                                      @RequestParam Long orderNumber) {
        log.info("Received request to delete order with number: {} by customer with email: {}", orderNumber, email);

        orderService.deleteOrder(orderNumber, email, password);

        log.info("Order with number: {} successfully deleted by customer with email: {}", orderNumber, email);
        return ResponseEntity.ok("Order with number " + orderNumber + " has been successfully deleted.");
    }
}