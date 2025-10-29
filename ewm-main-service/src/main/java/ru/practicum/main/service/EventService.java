package ru.practicum.main.service;

import client.ClientService;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.practicum.main.common.*;
import ru.practicum.main.dto.*;
import ru.practicum.main.mapper.*;
import ru.practicum.main.model.*;
import ru.practicum.main.storage.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Comparator.comparing;

@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final ClientService clientService;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final EventShortDtoMapper eventShortDtoMapper;
    private final EventViews eventViews;

    @Autowired
    EventService(EventRepository eventRepository, UserRepository userRepository,
                 CategoryRepository categoryRepository, ParticipationRequestRepository requestRepository,
                 RestTemplateBuilder builder, @Value("${stats-server.url}") String serverUrl,
                 EventFullDtoMapper eventFullDtoMapper,
                 EventShortDtoMapper eventShortDtoMapper,
                 EventViews eventViews) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.clientService = new ClientService(serverUrl, builder);
        this.eventFullDtoMapper = eventFullDtoMapper;
        this.eventShortDtoMapper = eventShortDtoMapper;
        this.eventViews = eventViews;
    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Getting events for user: {}, from: {}, size: {}", userId, from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь id = " + userId + " не найден"));
        return eventRepository.findByInitiatorId(userId)
                .stream()
                .skip(from)
                .limit(size)
                .map(eventShortDtoMapper::eventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория id = " + newEventDto.getCategory() + " не найдена"));
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Событие не может начинаться в ближайшие 2 часа");
        }
        return eventFullDtoMapper.eventToEventFullDto(eventRepository.save(NewEventDtoMapper.toEvent(newEventDto, category, user)));
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Getting event: {} for user: {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " не найдено");
        } else {
            return eventFullDtoMapper.eventToEventFullDto(event);
        }
    }

    private void updateEvent(Event event, UpdateEventRequest updateRequest, int preHour) {

        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(preHour))) {
                throw new BadRequestException("Событие не может начинатся в ближайшие " + preHour + " часа");
            }
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("категория id = " + updateRequest.getCategory() + " не найдена"));
            event.setCategory(category);
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

    }

    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Updating event: {} by user: {}", eventId, userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " пользователя id = " + userId + " не найдено");
        }
        updateEvent(event, updateRequest, 2);

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new DataConflictException("Событие в статусе " + event.getState().toString());
        }

        if (updateRequest.getStateAction() != null) {
            event.setState(updateRequest.getStateAction() == EventStateActionUser.SEND_TO_REVIEW ? EventState.PENDING : EventState.CANCELED);
        }

        return eventFullDtoMapper.eventToEventFullDto(eventRepository.save(event));
    }

    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        log.info("Getting participants for event: {} by user: {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " пользователя id = " + userId + " не найдено");
        } else {
            return requestRepository.findByEventId(eventId)
                    .stream()
                    .map(ParticipationRequestDtoMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
    }

    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        log.info("Changing request status for event: {} by user: {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);

        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " пользователя id = " + userId + " не найдено");
        } else if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataConflictException("У события = " + eventId + " достигнут лимит участников");
        } else {
            List<Long> requestIds = updateRequest.getRequestIds();

            List<ParticipationRequest> participationRequests = requestRepository.findByIdInAndStatus(requestIds, RequestStatus.PENDING);

            if (participationRequests.size() != requestIds.size()) {
                throw new DataConflictException("В списке заявок на подтверждение есть не в статусе ожидание");
            }

            List<ParticipationRequestDto> confirmedList = new ArrayList<>();
            List<ParticipationRequestDto> rejectList = new ArrayList<>();

            for (int i = 0; i < requestIds.size(); i++) {

                if (event.getConfirmedRequests() >= event.getParticipantLimit() || updateRequest.getStatus() == RequestStatus.REJECTED) {
                    participationRequests.get(i).setStatus(RequestStatus.REJECTED);
                    rejectList.add(ParticipationRequestDtoMapper.toParticipationRequestDto(participationRequests.get(i)));
                } else {
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    participationRequests.get(i).setStatus(RequestStatus.CONFIRMED);
                    confirmedList.add(ParticipationRequestDtoMapper.toParticipationRequestDto(participationRequests.get(i)));
                }

            }

            requestRepository.saveAll(participationRequests);
            eventRepository.save(event);
            return new EventRequestStatusUpdateResult(confirmedList, rejectList);
        }

    }

    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Getting events by admin with filters");

        BooleanExpression expression = null;

        if (!users.isEmpty()) {
            expression = expression == null ?
                    QEvent.event.initiator.id.in(users) :
                    expression.and(QEvent.event.initiator.id.in(users));
        }

        if (!states.isEmpty()) {
            expression = expression == null ?
                    QEvent.event.state.in(states) :
                    expression.and(QEvent.event.state.in(states));
        }

        if (!categories.isEmpty()) {
            expression = expression == null ?
                    QEvent.event.category.id.in(categories) :
                    expression.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null) {
            expression = expression == null ?
                    QEvent.event.eventDate.after(rangeStart) :
                    expression.and(QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            expression = expression == null ?
                    QEvent.event.eventDate.before(rangeEnd) :
                    expression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        return StreamSupport.stream((expression != null ? eventRepository.findAll(expression) : eventRepository.findAll())
                        .spliterator(), false)
                .skip(from)
                .limit(size)
                .map(eventFullDtoMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Updating event: {} by admin", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("событие id = " + eventId + " не найдено"));

        updateEvent(event, updateRequest, 1);

        if (event.getState() != EventState.PENDING) {
            throw new DataConflictException("Событие в статусе " + event.getState().toString());
        }
        if (event.getState() == EventState.PUBLISHED && updateRequest.getStateAction() == EventStateActionAdmin.REJECT_EVENT) {
            throw new DataConflictException("Событие в статусе опубликовано нельзя отменить");
        }

        if (updateRequest.getStateAction() != null) {
            event.setState(updateRequest.getStateAction() == EventStateActionAdmin.PUBLISH_EVENT ? EventState.PUBLISHED : EventState.CANCELED);
        }

        return eventFullDtoMapper.eventToEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               EventSort sort, Integer from, Integer size, HttpServletRequest request) {
        log.info("Getting public events with filters");
        clientService.hit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now(), null);

        if (!categories.isEmpty() && categoryRepository.findAllById(categories).size() < categories.size()) {
            throw new BadRequestException("Неверная категория");
        }

        List<Event> events;
        if (text.isEmpty()) {
            events = eventRepository.findByState(EventState.PUBLISHED);
        } else {
            events = eventRepository.findByText(text, text);
        }

        Comparator<Event> comparator = comparing(Event::getTitle);
        if (sort == null) {
            comparator = comparing(Event::getTitle);
        } else if (sort == EventSort.EVENT_DATE) {
            comparator = comparing(Event::getEventDate);
        } else if (sort == EventSort.VIEWS) {
            comparator = comparing(event -> eventViews.getViews(event.getId()));
        }

        return events.stream()
                .filter(item -> categories.isEmpty() || categories.contains(item.getCategory().getId()))
                .filter(item -> {
                    return (rangeStart == null) ? LocalDateTime.now().isBefore(item.getEventDate()) : rangeStart.isBefore(item.getEventDate());
                })
                .filter(item -> rangeEnd == null || rangeEnd.isAfter(item.getEventDate()))
                .filter(item -> paid == null || item.getPaid().equals(paid))
                .filter(item -> onlyAvailable == false || item.getParticipantLimit() > item.getConfirmedRequests())
                .sorted(comparator)
                .skip(from)
                .limit(size)
                .map(eventShortDtoMapper::eventShortDto)
                .collect(Collectors.toList());

    }

    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        log.info("Getting public event: {}", eventId);

        clientService.hit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now(), eventId);
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " не найдено");
        }
        return eventFullDtoMapper.eventToEventFullDto(event);
    }
}
