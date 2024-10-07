package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.cart.CartInput;
import org.example.coffee.dto.cart.CartOutput;
import org.example.coffee.entity.CartMapEntity;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.repository.CartMapRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartMapService {
    private final CartMapRepository cartMapRepository;
    private final CustomRepository customRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addProductToCart(String accessToken, CartInput cartInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (userEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        CartMapEntity cartMapEntityExist = cartMapRepository.findByProductIdAndUserId(cartInput.getProductId(), userId);
        if (cartMapEntityExist == null) {
            CartMapEntity cartMapEntity = CartMapEntity.builder()
                    .userId(userId)
                    .productId(cartInput.getProductId())
                    .quantityOrder(cartInput.getQuantityOrder())
                    .createdAt(LocalDateTime.now())
                    .build();

            cartMapRepository.save(cartMapEntity);
        } else {
            cartMapEntityExist.setQuantityOrder(cartInput.getQuantityOrder() + cartMapEntityExist.getQuantityOrder());
            cartMapRepository.save(cartMapEntityExist);
        }
    }

    @Transactional
    public void removeProductFromCart(String accessToken, Long cartId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (userEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        cartMapRepository.deleteById(cartId);
    }

    @Transactional(readOnly = true)
    public Page<CartOutput> getProductsInCart(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<CartMapEntity> cartMapEntities = cartMapRepository.findAllByUserId(userId, pageable);
        if (Objects.isNull(cartMapEntities) || cartMapEntities.isEmpty()) {
            return Page.empty();
        }

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(
                cartMapEntities.stream().map(CartMapEntity::getProductId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        return cartMapEntities.map(
                cartMapEntity -> {
                    ProductEntity productEntity = productEntityMap.get(cartMapEntity.getProductId());
                    return CartOutput.builder()
                            .cartId(cartMapEntity.getId())
                            .productId(productEntity.getId())
                            .quantityOrder(cartMapEntity.getQuantityOrder())
                            .nameProduct(productEntity.getName())
                            .price(productEntity.getPrice())
                            .imageUrl(productEntity.getImage())
                            .totalPrice(productEntity.getPrice() * cartMapEntity.getQuantityOrder())
                            .build();
                }
        );
    }

    @Transactional
    public List<CartOutput> getProductsBeforeOrdering(String accessToken, List<Long> cartIds) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<CartMapEntity> cartMapEntities = cartMapRepository.findAllByIdIn(cartIds);
        for (CartMapEntity cartMapEntity : cartMapEntities) {
            if (!cartMapEntity.getUserId().equals(userId)) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(
                cartMapEntities.stream().map(CartMapEntity::getProductId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        List<CartOutput> cartOutputs = new ArrayList<>();
        for (CartMapEntity cartMapEntity : cartMapEntities) {
            ProductEntity productEntity = productEntityMap.get(cartMapEntity.getProductId());
            CartOutput cartOutput = CartOutput.builder()
                    .cartId(cartMapEntity.getId())
                    .productId(productEntity.getId())
                    .quantityOrder(cartMapEntity.getQuantityOrder())
                    .nameProduct(productEntity.getName())
                    .price(productEntity.getPrice())
                    .totalPrice(productEntity.getPrice() * cartMapEntity.getQuantityOrder())
                    .build();
            cartOutputs.add(cartOutput);
        }
        return cartOutputs;
    }
}
