package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.product.ProductInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.entity.CommentEntity;
import org.example.coffee.entity.ProductCategoryMapEntity;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.ProductMapper;
import org.example.coffee.repository.CommentRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductCategoryRepository;
import org.example.coffee.repository.ProductRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CustomRepository customRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository productCategoryRepository;
    private final CommentRepository commentRepository;

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
    public Page<ProductOutput> getProducts(Pageable pageable) {
        Page<ProductEntity> products = productRepository.findAll(pageable);
        if (Objects.isNull(products) || products.isEmpty()) {
            return Page.empty();
        }

        return products.map(
                productEntity -> {
                     ProductOutput productOutput = ProductOutput.builder()
                             .productId(productEntity.getId())
                             .name(productEntity.getName())
                             .price(productEntity.getPrice())
                             .description(productEntity.getDescription())
                             .image(productEntity.getImage())
                             .build();

                     return productOutput;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductOutput> getProductsByCategory(Pageable pageable, Long categoryId) {
        List<Long> productIds = productCategoryRepository.findAllByCategoryId(categoryId)
                .stream().map(ProductCategoryMapEntity::getProductId).collect(Collectors.toList());

        Page<ProductEntity> products = productRepository.findAllByIdIn(productIds, pageable);
        if (Objects.isNull(products) || products.isEmpty()) {
            return Page.empty();
        }
        return products.map(
                productEntity -> {
                    return ProductOutput.builder()
                            .productId(productEntity.getId())
                            .name(productEntity.getName())
                            .price(productEntity.getPrice())
                            .description(productEntity.getDescription())
                            .image(productEntity.getImage())
                            .build();
                }
        );
    }

    @Transactional
    public void addProductToCategory(String accessToken, Long productId, Long categoryId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if (Boolean.TRUE.equals(productCategoryRepository.existsByCategoryIdAndProductId(categoryId, productId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductCategoryMapEntity productCategoryMapEntity = ProductCategoryMapEntity.builder()
                .categoryId(categoryId)
                .productId(productId)
                .build();

        productCategoryRepository.save(productCategoryMapEntity);
    }

    @Transactional
    public List<ProductOutput> getProductsNotInCategory(String accessToken, Long categoryId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductCategoryMapEntity> productCategoryMapEntities = productCategoryRepository
                .findAllByCategoryId(categoryId);
        List<Long> productIds = productCategoryMapEntities.stream()
                .map(ProductCategoryMapEntity::getProductId).collect(Collectors.toList());

        List<ProductEntity> productEntities = (productIds.isEmpty())
                ? productRepository.findAll() : productRepository.findAllByIdNotIn(productIds);
        List<ProductOutput> productOutputs = new ArrayList<>();
        for (ProductEntity productEntity : productEntities) {
            ProductOutput productOutput = ProductOutput.builder()
                    .productId(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .image(productEntity.getImage())
                    .build();
            productOutputs.add(productOutput);
        }
        return productOutputs;
    }

    @Transactional
    public void removeProductFromCategory(String accessToken, Long categoryId, Long productId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productCategoryRepository.deleteByCategoryIdAndProductId(categoryId, productId);
    }

    @Transactional(readOnly = true)
    public ProductOutput getProductDetails(Long productId) {
        ProductEntity productEntity = customRepository.getProductBy(productId);
        List<CommentEntity> commentEntities = commentRepository.findAllByProductId(productId);
        double averageRating = (double) Math.round(commentEntities
                .stream()
                .mapToLong(CommentEntity::getRating)
                .average().orElse(0.0) * 10) / 10;
        return ProductOutput.builder()
                .productId(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .description(productEntity.getDescription())
                .image(productEntity.getImage())
                .averageRatting(averageRating)
                .build();
    }
}
