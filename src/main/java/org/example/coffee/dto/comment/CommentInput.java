package org.example.coffee.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentInput {
    private Long productId;
    private String comment;
    private Long rating;
    private String images;
}
