package org.example.coffee.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserOrderInput {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String paymentMethod;
    private List<ProductOrderInput> productOrderInputs;
    private Integer totalPrice;
}
