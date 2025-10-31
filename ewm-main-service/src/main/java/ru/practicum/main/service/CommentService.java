package ru.practicum.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.common.BadRequestException;
import ru.practicum.main.common.CommentStatus;
import ru.practicum.main.common.ValidException;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.mapper.CommentDtoMapper;
import ru.practicum.main.mapper.NewCommentDtoMapper;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;
import ru.practicum.main.storage.CommentRepository;
import ru.practicum.main.storage.EventRepository;
import ru.practicum.main.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentDtoMapper commentDtoMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository,
                          CommentDtoMapper commentDtoMapper) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentDtoMapper = commentDtoMapper;
    }

    public CommentDto addCommentToEvent(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("addCommentToEvent eventId: {}, userId: {}", eventId, userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Событие id = " + eventId + " не найдено"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Пользователь id = " + userId + " не найден"));

        if (commentRepository.countByUserIdAndEventId(userId, eventId) > 0) {
            throw new ValidException("Пользователь может оставить только один комментарий к событию");
        }
        return commentDtoMapper.toCommentDto(commentRepository
                .save(NewCommentDtoMapper.toComment(newCommentDto.getContent(), event, user)));
    }

    public CommentDto updateCommentToEvent(Long commentId, NewCommentDto newCommentDto) {
        log.info("updateCommentToEvent commentId: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Комментарий id = " + commentId + " не найден"));

        comment.setContent(newCommentDto.getContent());
        comment.setStatus(CommentStatus.PENDING);
        return commentDtoMapper.toCommentDto(commentRepository.save(comment));
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
        return commentRepository.findByStatusAndEventId(CommentStatus.APPROVED, eventId)
                .stream()
                .map(commentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getPendingComments() {
        log.info("getPendingComments");
        return commentRepository.findByStatus(CommentStatus.PENDING)
                .stream()
                .map(commentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private CommentDto changeStatusComment(Long commentId, CommentStatus status) {
        log.info("changeStatusComment commentId: {} status: {}", commentId, status.toString());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Комментарий id = " + commentId + " не найден"));
        comment.setStatus(status);
        return commentDtoMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto approveComment(Long commentId) {
        log.info("approveComment commentId: {}", commentId);
        return changeStatusComment(commentId, CommentStatus.APPROVED);
    }

    public CommentDto rejectComment(Long commentId) {
        log.info("rejectComment commentId: {}", commentId);
        return changeStatusComment(commentId, CommentStatus.REJECTED);
    }

}
