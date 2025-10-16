package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.common.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "participation_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "requester_id"})
)
@EqualsAndHashCode(of = { "id" })
@Getter
@Setter
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RequestStatus status;

    @Column(name = "created")
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        if (status == null) status = RequestStatus.PENDING;
    }
}
