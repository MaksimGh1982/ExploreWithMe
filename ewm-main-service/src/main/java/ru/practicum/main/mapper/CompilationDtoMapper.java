package ru.practicum.main.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.model.Compilation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationDtoMapper {

    private final EventShortDtoMapper eventShortDtoMapper;

    @Autowired
    CompilationDtoMapper(EventShortDtoMapper eventShortDtoMapper) {
        this.eventShortDtoMapper = eventShortDtoMapper;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> events = new ArrayList<>();
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            events = compilation.getEvents()
                    .stream()
                    .map(eventShortDtoMapper::eventShortDto)
                    .collect(Collectors.toList());
        }
        return new CompilationDto(compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                events
        );
    }

}
