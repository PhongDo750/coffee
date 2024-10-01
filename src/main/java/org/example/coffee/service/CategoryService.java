package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.category.CategoryInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.entity.CategoryEntity;
import org.example.coffee.entity.ProductCategoryMapEntity;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.CategoryMapper;
import org.example.coffee.repository.CategoryRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductCategoryMapRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CustomRepository customRepository;
    private final CategoryMapper categoryMapper;
    private final ProductCategoryMapRepository productCategoryMapRepository;

    public void createCategory(String accessToken, CategoryInput categoryInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        CategoryEntity categoryEntity = categoryMapper.getEntityFromInput(categoryInput);
        categoryRepository.save(categoryEntity);
    }

    public List<ProductOutput> getProductsByCategory(Long categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<ProductCategoryMapEntity> productCategoryMaps = productCategoryMapRepository.findByCategory(categoryEntity);

        List<ProductOutput> productOutputs = new ArrayList<>();

        for (ProductCategoryMapEntity productCategoryMap : productCategoryMaps) {
            ProductEntity productEntity = productCategoryMap.getProduct();
            ProductOutput productOutput = ProductOutput.builder()
                    .productId(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .description(productEntity.getDescription())
                    .image(productEntity.getImage())
                    .build();
            productOutputs.add(productOutput);
        }
        return productOutputs;
    }
}
