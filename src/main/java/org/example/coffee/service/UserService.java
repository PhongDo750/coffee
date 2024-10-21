package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.cloudinary.CloudinaryHelper;
import org.example.coffee.common.Common;
import org.example.coffee.dto.user.*;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.UserMapper;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.UserRepository;
import org.example.coffee.token.TokenHelper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomRepository customRepository;

    @Transactional
    public String signUp(SignUpRequest signUpRequest) {
        if(Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new RuntimeException(Common.USERNAME_IS_EXISTS);
        }
        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = UserEntity.builder()
                .username(signUpRequest.getUsername())
                .password(signUpRequest.getPassword())
                .fullName(signUpRequest.getFullName())
                .email(signUpRequest.getEmail())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .build();
        userEntity.setImage(Common.IMAGE_DEFAULT);
        userEntity.setIsShop(Boolean.FALSE);
        userRepository.save(userEntity);
        return "True";
    }

    @Transactional
    public TokenResponse logIn(UserRequest logInRequest) {
        UserEntity userEntity = userRepository.findByUsername(logInRequest.getUsername());
        if(Objects.isNull(userEntity)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        String currentHashedPassword = userEntity.getPassword();
        if(BCrypt.checkpw(logInRequest.getPassword(),currentHashedPassword)) {
            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken(TokenHelper.generateToken(userEntity))
                    .isShop(userEntity.getIsShop())
                    .build();
            return tokenResponse;
        }
        throw new RuntimeException(Common.INCORRECT_PASSWORD);
    }

    @Transactional
    public void changeInformation(ChangeInfoUserRequest changeInfoUserRequest, String accessToken, MultipartFile multipartFile) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userMapper.updateEntityFromInput(userEntity,changeInfoUserRequest);
        userEntity.setPhoneNumber(changeInfoUserRequest.getPhoneNumber());
        if (Objects.nonNull(multipartFile) && !multipartFile.isEmpty()) {
            userEntity.setImage(CloudinaryHelper.uploadAndGetFileUrl(multipartFile));
        }
        userRepository.save(userEntity);
    }

    @Transactional(readOnly = true)
    public UserOutput getInformation(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        return UserOutput.builder()
                .fullName(userEntity.getFullName())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .imageUrl(userEntity.getImage())
                .build();
    }
}
