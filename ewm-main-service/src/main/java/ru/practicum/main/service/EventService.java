package ru.practicum.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.*;
import ru.practicum.main.storage.CategoryRepository;
import ru.practicum.main.storage.EventRepository;
import ru.practicum.main.storage.ParticipationRequestRepository;
import ru.practicum.main.storage.UserRepository;

import java.util.List;

@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    @Autowired
    EventService(EventRepository eventRepository, UserRepository userRepository,
                 CategoryRepository categoryRepository, ParticipationRequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Getting events for user: {}, from: {}, size: {}", userId, from, size);
        // TODO: реализовать пагинацию событий пользователя
        return List.of();
    }

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event for user: {}", userId);
        // TODO: проверить пользователя, категорию, дату события, сохранить
        return null;
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Getting event: {} for user: {}", eventId, userId);
        // TODO: найти событие пользователя или выбросить исключение
        return null;
    }

    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Updating event: {} by user: {}", eventId, userId);
        // TODO: проверить права, состояние события, обновить данные
        return null;
    }

    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        log.info("Getting participants for event: {} by user: {}", eventId, userId);
        // TODO: проверить права, получить заявки на участие
        return List.of();
    }

    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        log.info("Changing request status for event: {} by user: {}", eventId, userId);
        // TODO: проверить лимиты, обновить статусы заявок
        return null;
    }

    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        log.info("Getting events by admin with filters");
        // TODO: реализовать фильтрацию для администратора
        return List.of();
    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Updating event: {} by admin", eventId);
        // TODO: проверить состояние, даты, обновить данные
        return null;
    }

    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size) {
        log.info("Getting public events with filters");
        // TODO: реализовать публичную фильтрацию и поиск
        return List.of();
    }

    public EventFullDto getPublicEvent(Long eventId) {
        log.info("Getting public event: {}", eventId);
        // TODO: найти опубликованное событие или выбросить исключение
        return null;
    }

    public EventFullDto findEventById(Long eventId) {
        log.info("Finding event by id: {}", eventId);
        // TODO: найти событие или выбросить исключение
        return null;
    }
}
