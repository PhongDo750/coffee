package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.dto.user.ChangeInfoUserRequest;
import org.example.coffee.dto.user.TokenResponse;
import org.example.coffee.dto.user.UserOutput;
import org.example.coffee.dto.user.UserRequest;
import org.example.coffee.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Đăng kí tài khoản")
    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserRequest userRequest) {
        return userService.signUp(userRequest);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("/log-in")
    public TokenResponse logIn(@RequestBody UserRequest logInRequest) {
        return userService.logIn(logInRequest);
    }

    @Operation(summary = "Thay đổi thông tin người dùng")
    @PostMapping("/change-information")
    public void changeInformation(@RequestBody ChangeInfoUserRequest changeInfoUserRequest,
                                  @RequestHeader("Authorization") String accessToken) {
        userService.changeInformation(changeInfoUserRequest,accessToken);
    }

    @Operation(summary = "Lấy ra thông tin người dùng")
    @GetMapping("/get-information")
    public UserOutput getInformation(@RequestHeader("Authorization") String accessToken) {
        return userService.getInformation(accessToken);
    }
}
