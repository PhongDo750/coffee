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
    private final StateOrderRepository stateOrderRepository;
    private final UserRepository userRepository;

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
        return productOrdersOutputPage(userOrderEntities, pageable, state);
    }

    @Transactional
    public void cancelOrder(String accessToken, CancelOrderInput cancelOrderInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserOrderEntity userOrderEntity = customRepository.getUserOrder(cancelOrderInput.getOrderId());
        if (!userOrderEntity.getState().equals(Common.PENDING_PAYMENT) || !userId.equals(userOrderEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        userOrderEntity.setState(Common.CANCELED);
        StateOrderEntity stateOrderEntity = StateOrderEntity.builder()
                .orderId(userOrderEntity.getId())
                .reason(cancelOrderInput.getReason())
                .cancelerId(userOrderEntity.getUserId())
                .build();
        userOrderRepository.save(userOrderEntity);
        stateOrderRepository.save(stateOrderEntity);
    }

    public Page<ProductOrdersOutput> productOrdersOutputPage(List<UserOrderEntity> userOrderEntities, Pageable pageable, String state) {
        List<Long> orderIds = userOrderEntities.stream()
                .sorted(Comparator.comparing(UserOrderEntity::getCreatedAt).reversed())
                .map(UserOrderEntity::getId)
                .collect(Collectors.toList());
        Page<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByOrderIdIn(orderIds, pageable);
        if (Objects.isNull(productOrderMapEntities) || productOrderMapEntities.isEmpty()) {
            return Page.empty();
        }

        Map<Long, List<ProductOrderMapEntity>> productOrderMapEntityMap = productOrderMapEntities
                .stream().collect(Collectors.groupingBy(ProductOrderMapEntity::getOrderId));

        List<StateOrderEntity> stateOrderEntities = stateOrderRepository.findAllByOrderIdIn(orderIds);

        Map<Long, StateOrderEntity> stateOrderEntityMap = stateOrderEntities
                .stream().collect(Collectors.toMap(StateOrderEntity::getOrderId, Function.identity()));

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                stateOrderEntities.stream().map(StateOrderEntity::getCancelerId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<ProductOrdersOutput> productOrdersOutputs = new ArrayList<>();
        for (Long orderId : orderIds) {
            List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMapEntityMap.get(orderId);
            StateOrderEntity stateOrderEntity = stateOrderEntityMap.get(orderId);
            List<ProductOrderOutput> productOrderOutputs = new ArrayList<>();
            int totalPrice = 0;
            for (ProductOrderMapEntity productOrderMapEntity : productOrderMapEntityList) {
                ProductOrderOutput productOrderOutput = ProductOrderOutput.builder()
                        .productId(productOrderMapEntity.getProductId())
                        .productName(productOrderMapEntity.getNameProduct())
                        .image(productOrderMapEntity.getImage())
                        .quantityOrder(productOrderMapEntity.getQuantityOrder())
                        .price(productOrderMapEntity.getPrice())
                        .totalPrice(productOrderMapEntity.getTotalPrice())
                        .build();
                totalPrice += productOrderOutput.getTotalPrice();
                productOrderOutputs.add(productOrderOutput);
            }
            if (Objects.isNull(stateOrderEntity)) {
                ProductOrdersOutput productOrdersOutput = ProductOrdersOutput.builder()
                        .orderId(orderId)
                        .productOrderOutputs(productOrderOutputs)
                        .state(state)
                        .totalPrice(totalPrice)
                        .build();
                productOrdersOutputs.add(productOrdersOutput);
            } else {
                UserEntity userEntity = userEntityMap.get(stateOrderEntity.getCancelerId());
                CancelOrderOutput cancelOrderOutput = CancelOrderOutput.builder()
                        .reason(stateOrderEntity.getReason())
                        .name(userEntity.getFullName())
                        .build();
                ProductOrdersOutput productOrdersOutput = ProductOrdersOutput.builder()
                        .orderId(orderId)
                        .productOrderOutputs(productOrderOutputs)
                        .state(state)
                        .totalPrice(totalPrice)
                        .cancelOrderOutput(cancelOrderOutput)
                        .build();
                productOrdersOutputs.add(productOrdersOutput);
            }
        }

        return new PageImpl<>(productOrdersOutputs, pageable, productOrderMapEntities.getTotalElements());
    }
}
