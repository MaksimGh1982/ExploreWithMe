package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.common.EventState;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "category_Id")
    private Category category;

    @Column(name = "eventDate", nullable = false)
    private LocalDateTime eventDate;

    @Embedded
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "initiator_Id")
    private User initiator;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        if (paid == null) paid = false;
        if (participantLimit == null) participantLimit = 0;
        if (requestModeration == null) requestModeration = true;
        if (state == null) state = EventState.PENDING;
        if (confirmedRequests == null) confirmedRequests = 0L;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Location {

        @Column(name = "location_lat", nullable = false)
        private Float lat;

        @Column(name = "location_lon", nullable = false)
        private Float lon;
    }
}

