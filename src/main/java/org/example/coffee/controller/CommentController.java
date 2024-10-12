package org.example.coffee.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.example.coffee.dto.comment.CommentInput;
import org.example.coffee.dto.comment.CommentOutput;
import org.example.coffee.service.CommentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "tạo comment")
    @PostMapping("/create")
    public void createComment(@RequestHeader("Authorization") String accessToken,
                              @RequestBody CommentInput commentInput) {
        commentService.createComment(accessToken, commentInput);
    }

    @Operation(summary = "Update comment")
    @PutMapping("/update")
    public void updateComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long commentId,
                              @RequestBody CommentInput commentInput) {
        commentService.updateComment(accessToken, commentId, commentInput);
    }

    @Operation(summary = "Lấy ra comment")
    @GetMapping("/get-comments")
    public Page<CommentOutput> getCommentsByProduct(@RequestParam Long productId,
                                                    @ParameterObject Pageable pageable) {
        return commentService.getCommentsByProduct(productId, pageable);
    }

    @Operation(summary = "Xóa comment")
    @DeleteMapping("/delete")
    public void deleteComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long commentId
    ) {
        commentService.deleteComment(accessToken, commentId);
    }
}
