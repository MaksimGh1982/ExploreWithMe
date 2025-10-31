package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.model.*;
import ru.practicum.main.model.Event;

@UtilityClass
public class NewEventDtoMapper {
    public Event toEvent(NewEventDto newEventDto, Category category, User user) {

        Event event = new Event();

        Event.Location location = new Event.Location();
        location.setLat(newEventDto.getLocation().getLat());
        location.setLon(newEventDto.getLocation().getLon());

        event.setTitle(newEventDto.getTitle());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(newEventDto.getEventDate());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setInitiator(user);

        return event;
    }
}


