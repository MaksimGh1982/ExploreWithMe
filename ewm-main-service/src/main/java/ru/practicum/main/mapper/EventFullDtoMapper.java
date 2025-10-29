package ru.practicum.main.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.main.common.EventViews;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.model.Event;

@Component
public class EventFullDtoMapper {
    private final EventViews eventViews;

    @Autowired
    EventFullDtoMapper(EventViews eventViews) {
        this.eventViews = eventViews;
    }

    public EventFullDto eventToEventFullDto(Event event) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoryDtoMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toDtoLocation(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                eventViews.getViews(event.getId()));
    }
}
