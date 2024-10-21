package org.example.coffee.service;

import lombok.AllArgsConstructor;
import org.example.coffee.common.Common;
import org.example.coffee.dto.comment.CommentInput;
import org.example.coffee.dto.comment.CommentOutput;
import org.example.coffee.entity.CommentEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.helper.FileHelper;
import org.example.coffee.helper.StringUtils;
import org.example.coffee.mapper.CommentMapper;
import org.example.coffee.repository.CommentRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.UserRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CustomRepository customRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(String accessToken, CommentInput commentInput, List<MultipartFile> multipartFiles) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (userEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .userId(userId)
                .productId(commentInput.getProductId())
                .comment(commentInput.getComment())
                .rating(commentInput.getRating())
                .images(StringUtils.getStringFromList(FileHelper.getImageUrls(multipartFiles)))
                .createAt(LocalDateTime.now())
                .build();
        commentRepository.save(commentEntity);
    }

    @Transactional
    public void updateComment(String accessToken,
                              Long commentId,
                              CommentInput commentInput,
                              List<MultipartFile> multipartFiles) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        if (userEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        CommentEntity commentEntity = customRepository.getCommentBy(commentId);
        if (!commentEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        commentMapper.updateEntityFromInput(commentEntity, commentInput);
        commentEntity.setImages(StringUtils.getStringFromList(FileHelper.getImageUrls(multipartFiles)));
        commentRepository.save(commentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CommentOutput> getCommentsByProduct(Long productId, Pageable pageable) {
        Page<CommentEntity> commentEntities = commentRepository.findAllByProductId(productId, pageable);
        if (Objects.isNull(commentEntities) || commentEntities.isEmpty()) {
            return Page.empty();
        }

        Set<Long> userIds = commentEntities.stream().map(CommentEntity::getUserId).collect(Collectors.toSet());
        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));

        return commentEntities.map(
                commentEntity -> {
                    UserEntity userEntity = userEntityMap.get(commentEntity.getUserId());
                    return CommentOutput.builder()
                            .userId(commentEntity.getUserId())
                            .nameUser(userEntity.getFullName())
                            .image(userEntity.getImage())
                            .comment(commentEntity.getComment())
                            .rating(commentEntity.getRating())
                            .commentImages(StringUtils.getListFromString(commentEntity.getImages()))
                            .createdAt(commentEntity.getCreateAt())
                            .build();
                }
        );
    }

    @Transactional
    public void deleteComment(String accessToken, Long commentId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CommentEntity commentEntity = customRepository.getCommentBy(commentId);
        if (!commentEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        commentRepository.deleteById(commentId);
    }

}
