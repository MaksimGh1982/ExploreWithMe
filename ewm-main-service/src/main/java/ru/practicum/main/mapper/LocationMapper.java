package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.Location;
import ru.practicum.main.model.Event;

@UtilityClass
public class LocationMapper {
    public Location toDtoLocation(Event.Location eventLocation) {
        return new Location(eventLocation.getLat(), eventLocation.getLon());
    }

    public Event.Location toLocation(Location location) {
        return new Event.Location(location.getLat(), location.getLon());
    }
}
