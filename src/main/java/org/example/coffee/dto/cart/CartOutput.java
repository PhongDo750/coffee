package org.example.coffee.dto.cart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CartOutput {
    private Long id;
    private Long cartId;
    private Long productId;
    private String nameProduct;
    private Integer price;
    private Integer quantityOrder;
    private Integer totalPrice;
    private String imageUrl;
}
