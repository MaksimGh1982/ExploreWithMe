package ru.practicum.main.model;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.common.CommentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "Event_Id", nullable = false)
    private Event event;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }
}
