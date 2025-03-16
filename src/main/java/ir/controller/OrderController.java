package ir.controller;

import ir.dto.order.OrderRequestDto;
import ir.dto.order.OrderResponseDto;
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


}
