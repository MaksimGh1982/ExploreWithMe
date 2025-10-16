package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<EventShortDto>  findByCategoryId(Long Id);
}
