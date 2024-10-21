package org.example.coffee.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChangeInfoUserRequest {
    private String fullName;
    private String gender;
    private String email;
    private String phoneNumber;
}
