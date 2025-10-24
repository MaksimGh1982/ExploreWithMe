package ru.practicum.main.service;

import client.ClientService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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

@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final ViewsRepository viewsRepository;
    private final ClientService clientService;

    @Autowired
    EventService(EventRepository eventRepository, UserRepository userRepository,
                 CategoryRepository categoryRepository, ParticipationRequestRepository requestRepository,
                 ViewsRepository viewsRepository,
                 RestTemplateBuilder builder, @Value("${stats-server.url}") String serverUrl) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.viewsRepository = viewsRepository;
        this.clientService = new ClientService(serverUrl, builder);
    }

    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Getting events for user: {}, from: {}, size: {}", userId, from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь id = " + userId + " не найден"));
        return eventRepository.findByInitiatorId(userId)
                .stream()
                .skip(from)
                .limit(size)
                .map(item -> EventShortDtoMapper.eventShortDto(item))
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория id = " + newEventDto.getCategory() + " не найдена"));
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Событие не может начинатся в ближайшие 2 часа");
        }
        return EventFullDtoMapper.eventToEventFullDto(eventRepository.save(NewEventDtoMapper.toEvent(newEventDto, category, user)));
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Getting event: {} for user: {}", eventId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователь id = " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " не найдено");
        } else {
            return EventFullDtoMapper.eventToEventFullDto(event);
        }
    }

    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Updating event: {} by user: {}", eventId, userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " пользователя id = " + userId + " не найдено");
        }
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Событие не может начинатся в ближайшие 2 часа");
            }
        }
        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConstraintViolationException("Событие в статусе " + event.getState().toString(), null, null);
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
        if (updateRequest.getStateAction() != null) {
            event.setState(updateRequest.getStateAction() == EventStateActionUser.SEND_TO_REVIEW ? EventState.PENDING : EventState.CANCELED);
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        return EventFullDtoMapper.eventToEventFullDto(eventRepository.save(event));
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
                    .map(item -> ParticipationRequestDtoMapper.toParticipationRequestDto(item))
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
            throw new ConstraintViolationException("У события = " + eventId + " достигнут лимит участников", null, null);
        } else {
            List<Long> requestIds = updateRequest.getRequestIds();

            List<ParticipationRequest> participationRequests = requestRepository.findByIdInAndStatus(requestIds, RequestStatus.PENDING);

            if (participationRequests.size() != requestIds.size()) {
                throw new ConstraintViolationException("В списке заявок на подтверждение есть не в статусе ожидание", null, null);
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
        return eventRepository.findAll()
                .stream()
                .filter(item -> {
                    return users.isEmpty() ? true : users.contains(item.getInitiator().getId());
                })
                .filter(item -> {
                    return states.isEmpty() ? true : states.contains(item.getState());
                })
                .filter(item -> {
                    return categories.isEmpty() ? true : categories.contains(item.getCategory().getId());
                })
                .filter(item -> {
                    return (rangeStart == null) ? true : rangeStart.isBefore(item.getEventDate());
                })
                .filter(item -> {
                    return (rangeEnd == null) ? true : rangeEnd.isAfter(item.getEventDate());
                })
                .skip(from)
                .limit(size)
                .map(item -> EventFullDtoMapper.eventToEventFullDto(item))
                .collect(Collectors.toList());

    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Updating event: {} by admin", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("событие id = " + eventId + " не найдено"));
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Событие не может начинатся в ближайший час");
            }
        }
        if (event.getState() != EventState.PENDING) {
            throw new ConstraintViolationException("Событие в статусе " + event.getState().toString(), null, null);
        }
        if (event.getState() == EventState.PUBLISHED && updateRequest.getStateAction() == EventStateActionAdmin.REJECT_EVENT) {
            throw new ConstraintViolationException("Событие в статусе опубликовано нельзя отменить", null, null);
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
        if (updateRequest.getStateAction() != null) {
            event.setState(updateRequest.getStateAction() == EventStateActionAdmin.PUBLISH_EVENT ? EventState.PUBLISHED : EventState.CANCELED);
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        return EventFullDtoMapper.eventToEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               EventSort sort, Integer from, Integer size, HttpServletRequest request) {
        log.info("Getting public events with filters");
        clientService.hit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());


        if (!categories.isEmpty() && categoryRepository.findAllById(categories).size() < categories.size()) {
            throw new BadRequestException("Неверная категория");
        }

        List<Event> events;
        if (text.isEmpty()) {
            events = eventRepository.findByState(EventState.PUBLISHED);
        } else {
            events = eventRepository.findByText(text, text);
        }

        Comparator<Event> comparator = Comparator.comparing(Event::getTitle);
        if (sort == null) {
            comparator = Comparator.comparing(Event::getTitle);
        } else if (sort == EventSort.EVENT_DATE) {
            comparator = Comparator.comparing(Event::getEventDate);
        } else if (sort == EventSort.VIEWS) {
            comparator = Comparator.comparing(Event::getViews);
        }

        return events.stream()
                .filter(item -> {
                    return categories.isEmpty() ? true : categories.contains(item.getCategory().getId());
                })
                .filter(item -> {
                    return (rangeStart == null) ? LocalDateTime.now().isBefore(item.getEventDate()) : rangeStart.isBefore(item.getEventDate());
                })
                .filter(item -> {
                    return (rangeEnd == null) ? true : rangeEnd.isAfter(item.getEventDate());
                })
                .filter(item -> {
                    return (paid == null) ? true : item.getPaid().equals(paid);
                })
                .filter(item -> {
                    return (onlyAvailable == false) ? true : item.getParticipantLimit() > item.getConfirmedRequests();
                })
                .sorted(comparator)
                .skip(from)
                .limit(size)
                .map(item -> EventShortDtoMapper.eventShortDto(item))
                .collect(Collectors.toList());

    }

    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        log.info("Getting public event: {}", eventId);

        clientService.hit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event == null) {
            throw new EntityNotFoundException("событие id = " + eventId + " не найдено");
        } else {
            if (viewsRepository.countByIpAndEvent(request.getRemoteAddr(), eventId) == 0) {
                Views views = new Views();
                views.setEvent(eventId);
                views.setIp(request.getRemoteAddr());
                viewsRepository.save(views);
                event.setViews(event.getViews() + 1);
                eventRepository.save(event);
            }
            return EventFullDtoMapper.eventToEventFullDto(event);
        }
    }

}
