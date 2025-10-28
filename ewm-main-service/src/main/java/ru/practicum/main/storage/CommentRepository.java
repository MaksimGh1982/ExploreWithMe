package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.common.CommentStatus;
import ru.practicum.main.common.EventState;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByState(CommentStatus state);

    List<Comment> findByStateAndEventId(CommentStatus state, Long eventId);
}
