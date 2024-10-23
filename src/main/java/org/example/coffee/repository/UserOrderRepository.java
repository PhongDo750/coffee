package org.example.coffee.repository;

import org.example.coffee.entity.UserOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrderEntity, Long> {
    List<UserOrderEntity> findAllByUserIdAndState(Long userId, String state);

    List<UserOrderEntity> findAllByState(String state);
}
