package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.model.Comment;

@UtilityClass
public class CommentDtoMapper {
    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreated());
        commentDto.setStatus(comment.getStatus());
        commentDto.setUser(UserShortDtoMapper.toUserShortDto(comment.getUser()));
        commentDto.setEvent(EventShortDtoMapper.eventShortDto(comment.getEvent()));
        return commentDto;
    }

}
