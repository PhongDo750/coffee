package org.example.coffee.service.order;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.order.CancelOrderInput;
import org.example.coffee.dto.order.ProductOrderOutput;
import org.example.coffee.dto.order.ProductOrdersOutput;
import org.example.coffee.entity.ProductOrderMapEntity;
import org.example.coffee.entity.StateOrderEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.entity.UserOrderEntity;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductOrderMapRepository;
import org.example.coffee.repository.StateOrderRepository;
import org.example.coffee.repository.UserOrderRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShopOrderService {
    private final UserOrderRepository userOrderRepository;
    private final ProductOrderMapRepository productOrderMapRepository;
    private final CustomRepository customRepository;
    private final StateOrderRepository stateOrderRepository;

    @Transactional(readOnly = true)
    public Page<ProductOrdersOutput> getProductOrdersByState(String accessToken, Pageable pageable) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(shopId);
        if (userEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        List<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByState(Common.PENDING_PAYMENT);
        List<Long> orderIds = userOrderEntities.stream().map(UserOrderEntity::getId).collect(Collectors.toList());
        Page<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByOrderIdIn(orderIds, pageable);
        Map<Long, List<ProductOrderMapEntity>> productOrderMapEntityMap = productOrderMapEntities
                .stream().collect(Collectors.groupingBy(ProductOrderMapEntity::getOrderId));

        List<ProductOrdersOutput> productOrdersOutputs = new ArrayList<>();
        for (Long orderId : orderIds) {
            List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMapEntityMap.get(orderId);
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
                totalPrice += productOrderOutput.getPrice();
                productOrderOutputs.add(productOrderOutput);
            }
            ProductOrdersOutput productOrdersOutput = ProductOrdersOutput.builder()
                    .orderId(orderId)
                    .productOrderOutputs(productOrderOutputs)
                    .state(Common.PENDING_PAYMENT)
                    .totalPrice(totalPrice)
                    .build();
            productOrdersOutputs.add(productOrdersOutput);
        }
        return new PageImpl<>(productOrdersOutputs, pageable, productOrderMapEntities.getTotalElements());
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

        UserOrderEntity userOrderEntity = customRepository.getUserOrder(shopId);

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
