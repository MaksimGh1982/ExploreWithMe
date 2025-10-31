package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;

import java.util.List;

@UtilityClass
public class NewCompilationDtoMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setEvents(events);
        return compilation;
    }
}
