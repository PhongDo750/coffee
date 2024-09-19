package org.example.coffee.repository;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;

    public UserEntity getUserBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }
}
