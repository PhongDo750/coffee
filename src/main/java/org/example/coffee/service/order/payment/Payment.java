package org.example.coffee.service.order.payment;

import org.example.coffee.entity.UserOrderEntity;

public interface Payment {
    void payment(UserOrderEntity userOrderEntity);
}
