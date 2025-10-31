package ru.practicum.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CommentDto>> getPendingComments() {
        List<CommentDto> commentDTOs = commentService.getPendingComments();
        return ResponseEntity.ok(commentDTOs);
    }

    @PostMapping("/{commentId}/approve")
    public ResponseEntity<CommentDto> approveComment(@PathVariable Long commentId) {
        CommentDto approvedCommentDto = commentService.approveComment(commentId);
        return ResponseEntity.ok(approvedCommentDto);
    }

    @PostMapping("/{commentId}/reject")
    public ResponseEntity<CommentDto> rejectComment(@PathVariable Long commentId) {
        CommentDto rejectedCommentDto = commentService.rejectComment(commentId);
        return ResponseEntity.ok(rejectedCommentDto);
    }
}
