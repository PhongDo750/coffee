package org.example.coffee.repository;

import org.example.coffee.entity.StateOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateOrderRepository extends JpaRepository<StateOrderEntity, Long> {
    List<StateOrderEntity> findAllByOrderIdIn(List<Long> orderIds);
}
