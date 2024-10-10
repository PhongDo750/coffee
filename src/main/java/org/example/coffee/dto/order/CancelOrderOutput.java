package org.example.coffee.dto.order;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CancelOrderOutput {
    private Long cancelerId;
    private String reason;
}
