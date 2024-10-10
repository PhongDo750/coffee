package org.example.coffee.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductOrderInput {
    private Long cartId;
    private Long productId;
    private String nameProduct;
    private Integer price;
    private String image;
    private Integer quantityOrder;
    private Integer totalPrice;
}
