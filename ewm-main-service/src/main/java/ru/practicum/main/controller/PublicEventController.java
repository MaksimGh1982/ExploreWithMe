package ru.practicum.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.common.EventSort;
import ru.practicum.main.common.GlobalConstant;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = GlobalConstant.DATA_PATTERN)  LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = GlobalConstant.DATA_PATTERN)  LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_FROM) Integer from,
            @RequestParam(defaultValue = GlobalConstant.DEFAULT_SIZE) Integer size,
            HttpServletRequest request) {

        List<EventShortDto> events = eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {

        EventFullDto event = eventService.getPublicEvent(id, request);
        return ResponseEntity.ok(event);
    }
}