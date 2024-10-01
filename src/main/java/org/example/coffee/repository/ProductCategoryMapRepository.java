package org.example.coffee.repository;

import org.example.coffee.entity.CategoryEntity;
import org.example.coffee.entity.ProductCategoryMapEntity;
import org.example.coffee.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryMapRepository extends JpaRepository<ProductCategoryMapEntity, Long> {
    boolean existsByProductIdAndCategoryId(Long productId, Long categoryId);

    Optional<ProductCategoryMapEntity> findByProductAndCategory(ProductEntity productEntity, CategoryEntity categoryEntity);

    List<ProductCategoryMapEntity> findByProduct(ProductEntity productEntity);

    List<ProductCategoryMapEntity> findByCategory(CategoryEntity categoryEntity);
}
