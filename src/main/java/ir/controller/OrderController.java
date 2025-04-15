package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.bankcard.BankCardInfoDto;
import ir.dto.filter.ComprehensiveFilterDto;
import ir.dto.filter.ComprehensiveResponseDto;
import ir.dto.filter.FilterOrderByWorkingDateDto;
import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.enums.OrderStatus;
import ir.service.capcha.CaptchaService;
import ir.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final CaptchaService captchaService;

    @PostMapping("/create-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> saveCustomerOrder(@RequestHeader("Authorization") String token,
                                                              @Valid @RequestBody OrderRequestDto orderDto) {
        log.debug("Order details: {}", orderDto);

        OrderResponseDto orderResponseDto = orderService.saveOrder(orderDto, token);

        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/show-orders")
    @Operation(summary = "(Expert only)")
    public ResponseEntity<List<OrderResponseDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        log.info("Fetching all orders for expert with token");

        List<OrderResponseDto> orders = orderService.getOrder(token);

        log.info("Successfully retrieved {} orders for expert with token", orders.size());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/update-status")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> updateStatus(@RequestHeader("Authorization") String token,
                                                         @RequestParam OrderStatus status,
                                                         @RequestParam Long orderNumber,
                                                         @RequestParam Long offerNumber) {
        log.info("Received request to update order status for customer.");

        OrderResponseDto responseDto = orderService.updateOrderStatus(token, status, orderNumber, offerNumber);

        log.info("Order status successfully updated for order number: {}", orderNumber);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/orders_history")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrders(@RequestHeader("Authorization") String token) {
        log.info("Fetching order history for customer with token");

        List<OrderResponseDto> orders = orderService.getOrdersByCustomerEmail(token);

        log.info("Successfully retrieved order history with {} orders for customer with token",
                orders.size());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/paid")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> paid(@RequestHeader("Authorization") String token,
                                                 @RequestParam Long orderNumber,
                                                 @RequestParam Long offerNumber) {
        log.info("Processing payment for order number: {} by customer with token", orderNumber);

        OrderResponseDto orderResponseDto = orderService.paid(token, orderNumber, offerNumber);

        log.info("Payment successfully processed for order number: {} by customer with token", orderNumber);
        return ResponseEntity.ok(orderResponseDto);
    }

    @PutMapping("/paid/online")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<String> payOnline(@RequestHeader("Authorization") String token,
                                            @RequestParam Long orderNumber,
                                            @RequestParam Long offerNumber,
                                            @RequestParam String captchaId,
                                            @RequestParam String captchaCode,
                                            @Valid @RequestBody BankCardInfoDto bankCardInfoDto) {
        log.info("Processing payment for order number: {} by customer with token", orderNumber);

        if (!captchaService.verifyCaptcha(captchaId, captchaCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired captcha");
        }

        String message = orderService.payOnlineByCard(token, bankCardInfoDto, orderNumber, offerNumber);

        return ResponseEntity.ok(message);
    }

    @PutMapping("/update-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<OrderResponseDto> updateCustomerOrder(@RequestHeader("Authorization") String token,
                                                                @Valid @RequestBody OrderRequestDto orderDto,
                                                                @RequestParam Long orderId) {
        log.info("Received request to update order with ID: {} by customer with token", orderId);
        log.debug("Updated order details: {}", orderDto);

        OrderResponseDto updatedOrder = orderService.updateOrder(orderId, orderDto, token);

        log.info("Order with ID: {} successfully updated by customer with token", orderId);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/delete-order")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<String> deleteCustomerOrder(@RequestHeader("Authorization") String token,
                                                      @RequestParam Long orderNumber) {
        log.info("Received request to delete order with number: {} by customer with token", orderNumber);

        orderService.deleteOrder(orderNumber, token);

        log.info("Order with number: {} successfully deleted by customer with token", orderNumber);
        return ResponseEntity.ok("Order with number " + orderNumber + " has been successfully deleted.");
    }

    @GetMapping("/orders-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<Map<String, Long>> getOrderReport() {
        Map<String, Long> report = orderService.getOrderReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/filter-comprehensive-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<ComprehensiveResponseDto>> filterComprehensiveOrders(
            @Valid @RequestBody ComprehensiveFilterDto filterDto,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Filtering comprehensive orders with criteria: {}", filterDto);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<ComprehensiveResponseDto> comprehensiveResponseDtos = orderService.filterComprehensiveOrders(filterDto, pageable);

        log.info("Successfully filtered {} comprehensive orders.", comprehensiveResponseDtos.size());
        return ResponseEntity.ok(comprehensiveResponseDtos);
    }

    @PostMapping("/orders/working-date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByWorkingDate(
            @RequestBody FilterOrderByWorkingDateDto filterOrderByWorkingDateDto) {
        List<OrderResponseDto> orders = orderService.getOrdersByWorkingDate(filterOrderByWorkingDateDto);
        return ResponseEntity.ok(orders);
    }
}