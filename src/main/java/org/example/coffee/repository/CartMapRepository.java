package org.example.coffee.repository;

import org.example.coffee.entity.CartMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
CartMapRepository extends JpaRepository<CartMapEntity, Long> {
    Page<CartMapEntity> findAllByUserId(Long userId, Pageable pageable);

    List<CartMapEntity> findAllByIdIn(List<Long> cartIds);

    CartMapEntity findByProductIdAndUserId(Long productId, Long userId);

    void deleteAllByIdIn(List<Long> cartIds);
}
