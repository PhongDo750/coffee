package org.example.coffee.repository;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.entity.*;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartMapRepository cartMapRepository;
    private final UserOrderRepository userOrderRepository;
    private final CommentRepository commentRepository;

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

    public CategoryEntity getCategoryBy(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public CartMapEntity getCartMap(Long cartMapId) {
        return cartMapRepository.findById(cartMapId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public UserOrderEntity getUserOrder(Long orderId) {
        return userOrderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public CommentEntity getCommentBy(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }
}
