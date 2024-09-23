package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.product.ProductInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.ProductMapper;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CustomRepository customRepository;
    private final ProductMapper productMapper;

    @Transactional
    public void createProduct(String accessToken, ProductInput productInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductEntity productEntity = productMapper.getEntityFromInput(productInput);
        productRepository.save(productEntity);
    }

    @Transactional
    public void updateProduct(String accessToken, ProductInput productInput, Long productId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductEntity productEntity = customRepository.getProductBy(productId);
        productMapper.updateEntityFromInput(productEntity, productInput);
        productRepository.save(productEntity);
    }

    @Transactional
    public void deleteProduct(String accessToken, Long productId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productRepository.deleteById(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductOutput> getProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        List<ProductOutput> productOutputs = new ArrayList<>();
        for (ProductEntity productEntity : productEntities) {
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
