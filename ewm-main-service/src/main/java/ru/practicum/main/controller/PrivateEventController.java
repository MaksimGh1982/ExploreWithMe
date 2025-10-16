package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.*;
import ru.practicum.main.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        List<EventShortDto> events = eventService.getUserEvents(userId, from, size);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto) {

        EventFullDto event = eventService.addEvent(userId, newEventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        EventFullDto event = eventService.getUserEvent(userId, eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest updateRequest) {

        EventFullDto updatedEvent = eventService.updateEventByUser(userId, eventId, updateRequest);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        List<ParticipationRequestDto> requests = eventService.getEventParticipants(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {

        EventRequestStatusUpdateResult result = eventService.changeRequestStatus(userId, eventId, updateRequest);
        return ResponseEntity.ok(result);
    }
}
