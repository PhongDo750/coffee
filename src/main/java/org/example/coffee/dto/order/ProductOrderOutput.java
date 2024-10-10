package org.example.coffee.dto.order;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOrderOutput {
    private Long productId;
    private String productName;
    private Integer price;
    private String image;
    private Integer quantityOrder;
    private Integer totalPrice;
}
