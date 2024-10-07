package org.example.coffee.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentOutput {
    private Long userId;
    private Long productId;
    private String comment;
    private Long rating;
    private String images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
