package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.common.BadRequestException;
import ru.practicum.main.common.CommentStatus;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.mapper.CommentDtoMapper;
import ru.practicum.main.mapper.CompilationDtoMapper;
import ru.practicum.main.mapper.EventShortDtoMapper;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.storage.CommentRepository;
import ru.practicum.main.storage.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
    }

    public CommentDto addCommentToEvent(Long userId, Long eventId, CommentDto comment) {
        log.info("addCommentToEvent eventId: {}, userId: {}", eventId, userId);
        return null;
    }

    public CommentDto updateCommentToEvent(Long commentId, CommentDto comment) {
        log.info("updateCommentToEvent commentId: {}", commentId);
        return null;
    }

    public void deleteCommentToEvent(Long commentId) {
        log.info("deleteCommentToEvent commentId: {}", commentId);
        commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Комментарий id = " + commentId + " не найден"));
        commentRepository.deleteById(commentId);
    }

    public CommentDto getCommentById(Long commentId) {
        log.info("getCommentById commentId: {}", commentId);
        return null;
    }

    public List<CommentDto> getApprovedCommentsForEvent(Long eventId) {
        log.info("getApprovedCommentsForEvent eventId: {}", eventId);
        return commentRepository.findByStateAndEventId(CommentStatus.APPROVED, eventId)
                .stream()
                .map((item) -> CommentDtoMapper.toCommentDto(item))
                .collect(Collectors.toList());
    }

    public List<CommentDto> getPendingComments() {
        log.info("getPendingComments");
        return commentRepository.findByState(CommentStatus.PENDING)
                .stream()
                .map((item) -> CommentDtoMapper.toCommentDto(item))
                .collect(Collectors.toList());
    }

    private CommentDto changeStatusComment(Long commentId, CommentStatus status) {
        log.info("changeStatusComment commentId: {} status: {}", commentId, status.toString());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Комментарий id = " + commentId + " не найден"));
        comment.setStatus(status);
        return CommentDtoMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto approveComment(Long commentId) {
        log.info("approveComment commentId: {}", commentId);
        return changeStatusComment(Long commentId, CommentStatus.APPROVED);
    }

    public CommentDto rejectComment(Long commentId) {
        log.info("rejectComment commentId: {}", commentId);
        return changeStatusComment(Long commentId, CommentStatus.REJECTED);
    }

}
