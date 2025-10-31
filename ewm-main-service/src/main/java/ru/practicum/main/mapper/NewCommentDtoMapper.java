package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.common.CommentStatus;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;

@UtilityClass
public class NewCommentDtoMapper {
    public Comment toComment(String content, Event event, User user) {
        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setUser(user);
        comment.setContent(content);
        comment.setStatus(CommentStatus.PENDING);
        return comment;
    }
}
