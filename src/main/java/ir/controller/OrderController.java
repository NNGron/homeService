package ir.controller;

import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.enums.OrderStatus;
import ir.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/save-customer-order")
    public ResponseEntity<OrderResponseDto> saveCustomerOrder(@Valid @RequestBody OrderRequestDto orderDto,
                                                      @RequestParam String email,
                                                      @RequestParam String password) {

        OrderResponseDto orderResponseDto = orderService.saveOrder(orderDto, email, password);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/show-orders-for-expert")
    public ResponseEntity<List<OrderResponseDto>> getAllUsers(@RequestParam String email, @RequestParam String password) {
        List<OrderResponseDto> orders = orderService.getOrder(email, password);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/update-status")
    public ResponseEntity<OrderResponseDto> updateStatus(@RequestParam String email,
                                                         @RequestParam String password,
                                                         @RequestParam OrderStatus status,
                                                         @RequestParam Long orderNumber,
                                                         @RequestParam Long offerNumber) {
        OrderResponseDto orderResponseDto = orderService.updateOrderStatus(email, password, status, orderNumber, offerNumber);
        return ResponseEntity.ok(orderResponseDto);
    }

    @PutMapping("/paid")
    public ResponseEntity<OrderResponseDto> paid(@RequestParam String email,
                                                 @RequestParam String password,
                                                 @RequestParam Long orderNumber,
                                                 @RequestParam Long offerNumber) {
        OrderResponseDto orderResponseDto = orderService.paid(email, password, orderNumber, offerNumber);
        return ResponseEntity.ok(orderResponseDto);
    }
}
