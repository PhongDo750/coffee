package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.service.order.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Thanh toán")
    @PostMapping()
    public ResponseEntity<String> processPayment(@RequestHeader("Authorization") String accessToken,
                                                 @RequestParam Long orderId,
                                                 @RequestParam String method) {
        return ResponseEntity.ok(paymentService.processPayment(accessToken,method,orderId));
    }
}
