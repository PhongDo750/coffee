package org.example.coffee.repository;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserEntity getUserBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductEntity getProductBy(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }
}
