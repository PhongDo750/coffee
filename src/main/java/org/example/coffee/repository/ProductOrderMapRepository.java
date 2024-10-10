package org.example.coffee.repository;

import org.example.coffee.entity.ProductOrderMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderMapRepository extends JpaRepository<ProductOrderMapEntity, Long> {
    Page<ProductOrderMapEntity> findAllByOrderIdIn(List<Long> orderId, Pageable pageable);


}
