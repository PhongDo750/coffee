package org.example.coffee.service.order.payment;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.entity.UserOrderEntity;
import org.example.coffee.repository.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VNPAYPayment implements Payment {
    private final UserOrderRepository userOrderRepository;

    @Override
    public void payment(UserOrderEntity userOrderEntity) {
        userOrderEntity.setPaymentMethod("Thanh toán online");
        userOrderEntity.setState(Common.WAITING_DELIVERY);
        userOrderRepository.save(userOrderEntity);
    }
}
