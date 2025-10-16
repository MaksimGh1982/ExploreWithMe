package ru.practicum.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;
import ru.practicum.main.storage.CompilationRepository;
import ru.practicum.main.storage.EventRepository;

import java.util.List;

@Service
@Slf4j
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    CompilationService(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Getting compilations with pinned: {}, from: {}, size: {}", pinned, from, size);
        // TODO: реализовать пагинацию и фильтрацию по pinned
        return List.of();
    }

    public CompilationDto getCompilation(Long compId) {
        log.info("Getting compilation by id: {}", compId);
        // TODO: найти подборку или выбросить исключение
        return null;
    }

    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        log.info("Saving new compilation: {}", newCompilationDto.getTitle());
        // TODO: проверить уникальность названия, сохранить подборку
        return null;
    }

    public void deleteCompilation(Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        // TODO: проверить существование, удалить подборку
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        log.info("Updating compilation with id: {}", compId);
        // TODO: найти подборку, обновить данные
        return null;
    }
}
