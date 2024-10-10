package org.example.coffee.service.order.payment;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.entity.UserOrderEntity;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentService {
    private final CustomRepository customRepository;
    private final PaymentMethod paymentMethod;

    @Transactional
    public String processPayment(String accessToken, String method, Long orderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserOrderEntity userOrderEntity = customRepository.getUserOrder(orderId);
        if (!userOrderEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        Payment payment = paymentMethod.findPaymentMethod(method);
        payment.payment(userOrderEntity);

        return Common.SUCCESS;
    }
}
