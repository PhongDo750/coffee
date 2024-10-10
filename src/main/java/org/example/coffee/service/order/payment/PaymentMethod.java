package org.example.coffee.service.order.payment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentMethod {
    private final VNPAYPayment vnpayPayment;
    private final CashPayment cashPayment;

    public Payment findPaymentMethod(String paymentMethod) {
        if (paymentMethod.equals("cash")) {
            return cashPayment;
        }
        return vnpayPayment;
    }
}
