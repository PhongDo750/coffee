package org.example.coffee.service.order;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.order.CancelOrderInput;
import org.example.coffee.dto.order.CancelOrderOutput;
import org.example.coffee.dto.order.ProductOrderOutput;
import org.example.coffee.dto.order.ProductOrdersOutput;
import org.example.coffee.entity.ProductOrderMapEntity;
import org.example.coffee.entity.StateOrderEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.entity.UserOrderEntity;
import org.example.coffee.repository.*;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShopOrderService {
    private final UserOrderRepository userOrderRepository;
    private final CustomRepository customRepository;
    private final StateOrderRepository stateOrderRepository;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public Page<ProductOrdersOutput> getProductOrdersByState(String accessToken, Pageable pageable, String state) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByState(state);
        return orderService.productOrdersOutputPage(userOrderEntities, pageable, state);
    }

    @Transactional
    public void acceptOrder(String accessToken, Long orderId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(shopId);
        if (userEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        UserOrderEntity userOrderEntity = customRepository.getUserOrder(orderId);
        userOrderEntity.setState(Common.WAITING_DELIVERY);
        userOrderRepository.save(userOrderEntity);
    }

    @Transactional
    public void cancelOrderByShop(String accessToken, CancelOrderInput cancelOrderInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(shopId);
        if (userEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        UserOrderEntity userOrderEntity = customRepository.getUserOrder(cancelOrderInput.getOrderId());

        userOrderEntity.setState(Common.CANCELED);
        StateOrderEntity stateOrderEntity = StateOrderEntity.builder()
                .orderId(userOrderEntity.getId())
                .reason(cancelOrderInput.getReason())
                .cancelerId(shopId)
                .build();
        userOrderRepository.save(userOrderEntity);
        stateOrderRepository.save(stateOrderEntity);
    }
}
