package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.common.CommentStatus;
import ru.practicum.main.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByStatus(CommentStatus state);

    List<Comment> findByStatusAndEventId(CommentStatus state, Long eventId);

    Integer countByUserIdAndEventId(Long userId, Long eventId);
}
