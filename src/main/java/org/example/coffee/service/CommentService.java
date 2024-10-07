package org.example.coffee.service;

import org.example.coffee.dto.comment.CommentInput;
import org.example.coffee.dto.comment.CommentOutput;
import org.example.coffee.entity.CommentEntity;
import org.example.coffee.entity.UserEntity;
import org.example.coffee.mapper.CommentMapper;
import org.example.coffee.repository.CommentRepository;
import org.example.coffee.repository.CustomRepository;
import org.example.coffee.repository.UserRepository;
import org.example.coffee.token.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CustomRepository customRepository;

    @Transactional
    public void createComment(String accessToken, CommentInput commentInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User must be logged in to create a comment");
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .userId(userId)
                .productId(commentInput.getProductId())
                .comment(commentInput.getComment())
                .rating(commentInput.getRating())
                .images(commentInput.getImages())
                .createAt(LocalDateTime.now())
                .build();
        commentRepository.save(commentEntity);
    }

    @Transactional
    public void updateComment(String accessToken,
                              Long id,
                              CommentInput commentInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            throw new RuntimeException("User must be logged in to create a comment");
        }

        Optional<CommentEntity> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()){
            throw new RuntimeException("Comment not found");
        }

        CommentEntity commentEntity = optionalComment.get();
        commentMapper.updateEntityFromInput(commentEntity, commentInput);
        commentEntity.setUpdateAt(LocalDateTime.now());
        commentRepository.save(commentEntity);
    }

    @Transactional
    public Page<CommentOutput> getCommentsByProduct(Long productId, Pageable pageable) {
        Page<CommentEntity> comments = commentRepository.findAllByProductId(productId, pageable);
        if (Objects.isNull(comments) || comments.isEmpty()) {
            return Page.empty();
        }

        return comments.map(
                commentEntity -> {
                    return CommentOutput.builder()
                            .userId(commentEntity.getUserId())
                            .productId(commentEntity.getProductId())
                            .comment(commentEntity.getComment())
                            .rating(commentEntity.getRating())
                            .images(commentEntity.getImages())
                            .createdAt(commentEntity.getCreateAt())
                            .updatedAt(commentEntity.getUpdateAt())
                            .build();
                }
        );
    }

    @Transactional
    public void deleteComment(String accessToken, Long id) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity shopEntity = customRepository.getUserBy(userId);

        if (user == null) {
            throw new RuntimeException("User must be logged in to delete a comment");
        }
        if (!Objects.equals(userId, commentRepository.findById(id).get().getUserId()) && shopEntity.getIsShop().equals(Boolean.FALSE)) {
            System.out.println(userId + " " + commentRepository.findById(id).get().getUserId());
            throw new RuntimeException("You only have permission to delete your comment");
        }

        commentRepository.deleteById(id);
    }
}
