package ru.practicum.main.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.model.Comment;

@Component
public class CommentDtoMapper {

    private final EventShortDtoMapper eventShortDtoMapper;

    @Autowired
    CommentDtoMapper(EventShortDtoMapper eventShortDtoMapper) {
        this.eventShortDtoMapper = eventShortDtoMapper;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreated());
        commentDto.setStatus(comment.getStatus());
        commentDto.setUser(UserShortDtoMapper.toUserShortDto(comment.getUser()));
        commentDto.setEvent(eventShortDtoMapper.eventShortDto(comment.getEvent()));
        return commentDto;
    }

}
