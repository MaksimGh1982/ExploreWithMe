package ru.practicum.main.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.main.common.EventViews;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.model.Event;

@Component
public class EventShortDtoMapper {

    private final EventViews eventViews;

    @Autowired
    EventShortDtoMapper(EventViews eventViews) {
        this.eventViews = eventViews;
    }

    public EventShortDto eventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryDtoMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                eventViews.getViews(event.getId()));
    }
}
