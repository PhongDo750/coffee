package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.dto.order.CancelOrderInput;
import org.example.coffee.dto.order.ProductOrdersOutput;
import org.example.coffee.service.order.ShopOrderService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/shop-order")
public class ShopOrderController {
    private final ShopOrderService shopOrderService;

    @Operation(summary = "Lấy đơn hàng theo trạng thái")
    @GetMapping("/get-orders")
    public Page<ProductOrdersOutput> getProductOrdersByState(@RequestHeader("Authorization") String accessToken,
                                                             @ParameterObject Pageable pageable,
                                                             @RequestParam String state) {
        return shopOrderService.getProductOrdersByState(accessToken, pageable, state);
    }

    @Operation(summary = "Xác nhận đơn hàng")
    @PostMapping("/accept-order")
    public void acceptOrder(@RequestHeader("Authorization") String accessToken,
                            @RequestParam Long orderId) {
        shopOrderService.acceptOrder(accessToken, orderId);
    }

    @Operation(summary = "Không nhận đơn hàng")
    @PostMapping("/cancel-order")
    public void cancelOrderByShop(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody CancelOrderInput cancelOrderInput) {
        shopOrderService.cancelOrderByShop(accessToken, cancelOrderInput);
    }
}
