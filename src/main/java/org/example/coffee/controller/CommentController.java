package org.example.coffee.controller;

import lombok.AllArgsConstructor;
import org.example.coffee.dto.comment.CommentInput;
import org.example.coffee.dto.comment.CommentOutput;
import org.example.coffee.service.CommentService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/comment")
@AllArgsConstructor
@CrossOrigin
public class CommentController {
    @Autowired
    private final CommentService commentService;

    @PostMapping("/create")
    public void createComment(@RequestHeader("Authorization") String accessToken,
                              @RequestBody CommentInput commentInput) {
        commentService.createComment(accessToken, commentInput);
    }

    @PutMapping("/update")
    public void updateComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long id,
                              @RequestBody CommentInput commentInput) {
        commentService.updateComment(accessToken, id, commentInput);
    }

    @GetMapping("/get-comments")
    public Page<CommentOutput> getCommentsByProduct(@RequestParam Long productId,
                                                    @ParameterObject Pageable pageable) {
        return commentService.getCommentsByProduct(productId, pageable);
    }

    @DeleteMapping("/delete")
    public void deleteComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long id
    ) {
        commentService.deleteComment(accessToken, id);
    }
}
