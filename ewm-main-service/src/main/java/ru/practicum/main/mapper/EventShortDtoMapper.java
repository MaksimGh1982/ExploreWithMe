package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.model.Event;

@UtilityClass
public class EventShortDtoMapper {
    public EventShortDto eventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryDtoMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserShortDtoMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews());

        return eventShortDto;
    }
}
