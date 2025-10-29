package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.common.BadRequestException;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.dto.UpdateCompilationRequest;
import ru.practicum.main.mapper.CompilationDtoMapper;
import ru.practicum.main.mapper.NewCompilationDtoMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.storage.CompilationRepository;
import ru.practicum.main.storage.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationDtoMapper compilationDtoMapper;

    @Autowired
    CompilationService(CompilationRepository compilationRepository,
                       EventRepository eventRepository,
                       CompilationDtoMapper compilationDtoMapper) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.compilationDtoMapper = compilationDtoMapper;
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Getting compilations with pinned: {}, from: {}, size: {}", pinned, from, size);
        return compilationRepository.findAll()
                .stream()
                .filter(item -> pinned == null || item.getPinned() == pinned)
                .skip(from)
                .limit(size)
                .map((item) -> compilationDtoMapper.toCompilationDto(item))
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilation(Long compId) {
        log.info("Getting compilation by id: {}", compId);
        return compilationDtoMapper.toCompilationDto(compilationRepository
                .findById(compId).orElseThrow(() -> new EntityNotFoundException("Подборка id = " + compId + " не найдена")));

    }

    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        log.info("Saving new compilation: {}", newCompilationDto.getTitle());
        List<Event> events = null;
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = newCompilationDto.getEvents()
                    .stream()
                    .map(item -> eventRepository.findById(item)
                            .orElseThrow(() -> new BadRequestException("событие id = " + item + " не найдено")))
                    .collect(Collectors.toList());
        }
        return compilationDtoMapper.toCompilationDto(compilationRepository
                .save(NewCompilationDtoMapper.toCompilation(newCompilationDto, events)));
    }

    public void deleteCompilation(Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        getCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        log.info("Updating compilation with id: {}", compId);
        Compilation oldCompilation = compilationRepository
                .findById(compId).orElseThrow(() -> new EntityNotFoundException("Подборка id = " + compId + " не найдена"));

        List<Event> events = null;
        if (updateRequest.getEvents() != null) {
            events = updateRequest.getEvents()
                    .stream()
                    .map(item -> eventRepository.findById(item)
                            .orElseThrow(() -> new BadRequestException("событие id = " + item + " не найдено")))
                    .collect(Collectors.toList());
            oldCompilation.setEvents(events);
        }
        if (updateRequest.getPinned() != null) {
            oldCompilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            oldCompilation.setTitle(updateRequest.getTitle());
        }

        return compilationDtoMapper.toCompilationDto(compilationRepository.save(oldCompilation));
    }
}
