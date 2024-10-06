package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.category.CategoryInput;
import org.example.coffee.dto.category.CategoryOutput;
import org.example.coffee.dto.product.ProductInput;
import org.example.coffee.dto.product.ProductOutput;
import org.example.coffee.dto.productCategoryMap.ProductCategoryMapRequest;
import org.example.coffee.entity.CategoryEntity;
import org.example.coffee.entity.ProductCategoryMapEntity;
import org.example.coffee.entity.ProductEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.ProductMapper;
import org.example.coffee.repository.CategoryRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.ProductCategoryMapRepository;
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
    private final CategoryRepository categoryRepository;
    private final ProductCategoryMapRepository productCategoryMapRepository;

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

    public void addProductToCategory(
            String accessToken,
            ProductCategoryMapRequest productCategoryMapRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        Long productId = productCategoryMapRequest.getProductId();
        Long categoryId = productCategoryMapRequest.getCategoryId();

        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (productCategoryMapRepository.existsByProductIdAndCategoryId(
                productId,
                categoryId)) {
            throw new RuntimeException("Product already added to this category");
        }

        ProductCategoryMapEntity productCategoryMap = new ProductCategoryMapEntity();
        productCategoryMap.setProduct(productEntity);
        productCategoryMap.setCategory(categoryEntity);

        productCategoryMapRepository.save(productCategoryMap);
    }

    public void removeProductFromCategory(
            String accessToken,
            ProductCategoryMapRequest productCategoryMapRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        Long productId = productCategoryMapRequest.getProductId();
        Long categoryId = productCategoryMapRequest.getCategoryId();

        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ProductCategoryMapEntity productCategoryMap = productCategoryMapRepository
                .findByProductAndCategory(productEntity, categoryEntity)
                .orElseThrow(() -> new RuntimeException("Product-Category relation not found"));

        productCategoryMapRepository.delete(productCategoryMap);
    }

    public List<CategoryOutput> getCategoriesByProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductCategoryMapEntity> productCategoryMaps =
                productCategoryMapRepository.findByProduct(productEntity);
        List<CategoryOutput> categoryResponses = new ArrayList<>();

        for (ProductCategoryMapEntity productCategoryMap : productCategoryMaps) {
            CategoryEntity category = productCategoryMap.getCategory();
            CategoryOutput categoryResponse = new CategoryOutput(
                    category.getId(),
                    category.getName());
            categoryResponses.add(categoryResponse);
        }
        return categoryResponses;
    }
}
