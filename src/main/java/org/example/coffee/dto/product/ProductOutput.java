package org.example.coffee.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOutput {
    private Long id;
    private Long productId;
    private String name;
    private String description;
    private Integer price;
    private String image;
    private Double averageRatting;
}
