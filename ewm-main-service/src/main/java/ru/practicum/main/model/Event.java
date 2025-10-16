package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.common.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@EqualsAndHashCode(of = { "id" })
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    @Column(name = "description", length = 7000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Embedded
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private EventState state;

    @Column(name = "views")
    private Long views;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<ParticipationRequest> participationRequests;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        if (paid == null) paid = false;
        if (participantLimit == null) participantLimit = 0;
        if (requestModeration == null) requestModeration = true;
        if (state == null) state = EventState.PENDING;
        if (views == null) views = 0L;
        if (confirmedRequests == null) confirmedRequests = 0L;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Location {

        @Column(name = "location_lat", nullable = false)
        private Float lat;

        @Column(name = "location_lon", nullable = false)
        private Float lon;
    }
}

