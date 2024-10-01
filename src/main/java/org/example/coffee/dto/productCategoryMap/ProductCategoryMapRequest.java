package org.example.coffee.dto.productCategoryMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryMapRequest {
    private Long productId;
    private Long categoryId;
}
