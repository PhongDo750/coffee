package org.example.coffee.service.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.order.*;
import org.example.coffee.entity.*;
import org.example.coffee.mapper.ProductOrderMapper;
import org.example.coffee.mapper.UserOrderMapper;
import org.example.coffee.repository.*;
import org.example.coffee.service.order.payment.Payment;
import org.example.coffee.service.order.payment.PaymentMethod;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final UserOrderRepository userOrderRepository;
    private final ProductOrderMapRepository productOrderMapRepository;
    private final ProductOrderMapper productOrderMapper;
    private final UserOrderMapper userOrderMapper;
    private final CartMapRepository cartMapRepository;
    private final CustomRepository customRepository;
    private final StateGeneration stateGeneration;

    @Transactional
    public UserOrderOutput orderProducts(String accessToken, UserOrderInput userOrderInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserOrderEntity userOrderEntity = userOrderMapper.getEntityFromInput(userOrderInput);
        userOrderEntity.setState(Common.PENDING_PAYMENT);
        userOrderEntity.setUserId(userId);
        userOrderEntity.setCreatedAt(LocalDateTime.now());
        userOrderRepository.save(userOrderEntity);

        List<Long> cartIds = userOrderInput.getProductOrderInputs().stream()
                .map(ProductOrderInput::getCartId).collect(Collectors.toList());

        List<CartMapEntity> cartMapEntities = cartMapRepository.findAllByIdIn(cartIds);
        if (cartMapEntities == null || cartMapEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        cartMapRepository.deleteAllByIdIn(cartIds);

        for (ProductOrderInput productOrderInput : userOrderInput.getProductOrderInputs()) {
            ProductOrderMapEntity productOrderMapEntity = productOrderMapper.getEntityFromInput(productOrderInput);
            productOrderMapEntity.setOrderId(userOrderEntity.getId());
            productOrderMapRepository.save(productOrderMapEntity);
        }

        return UserOrderOutput.builder()
                .orderId(userOrderEntity.getId())
                .amount(userOrderEntity.getTotalPrice())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ProductOrdersOutput> getProductOrdersByState(String accessToken, String state, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByUserIdAndState(userId, state);
        StateOrder stateOrder = stateGeneration.findSateBy(state);
        return stateOrder.getOrders(userOrderEntities, pageable, state);
    }

    @Transactional
    public void cancelOrder(String accessToken, CancelOrderInput cancelOrderInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserOrderEntity userOrderEntity = customRepository.getUserOrder(cancelOrderInput.getOrderId());
        if (!userOrderEntity.getState().equals(Common.PENDING_PAYMENT) || !userId.equals(userOrderEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        userOrderEntity.setState(Common.CANCELED);
        userOrderEntity.setCancelerId(userId);
        userOrderEntity.setReasonCancellation(cancelOrderInput.getReason());
        userOrderRepository.save(userOrderEntity);
    }
}
