package ru.practicum.main.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @Autowired
    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto request) {

        CommentDto savedCommentDto = commentService.addCommentToEvent(userId, eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCommentDto);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody NewCommentDto request) {

        CommentDto savedCommentDto = commentService.updateCommentToEvent(commentId, request);
        return ResponseEntity.ok(savedCommentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentToEvent(commentId);
    }
}
