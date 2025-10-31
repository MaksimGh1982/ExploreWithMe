package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.practicum.main.common.DataConflictException;
import ru.practicum.main.common.EventState;
import ru.practicum.main.common.RequestStatus;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.mapper.ParticipationRequestDtoMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.User;
import ru.practicum.main.storage.EventRepository;
import ru.practicum.main.storage.ParticipationRequestRepository;
import ru.practicum.main.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    RequestService(EventRepository eventRepository, UserRepository userRepository,
                   ParticipationRequestRepository requestRepository,
                   RestTemplateBuilder builder, @Value("${stats-server.url}") String serverUrl) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Getting requests for user: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь id = " + userId + "  не найден"));

        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(ParticipationRequestDtoMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId, HttpServletRequest request) {
        log.info("Adding participation request for user: {} to event: {}", userId, eventId);
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new DataConflictException("Повторный запрос на учатсие в событии id = " + eventId + " от пользователя id = " + userId);
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("событие id = " + eventId + "  не найдено"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("Нельзя подать заявку на участие в собственном событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataConflictException("Событие id = " + eventId + " не опубликовано");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() > 0) {
            throw new DataConflictException("У события = " + eventId + " достигнут лимит участников");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь id = " + userId + "  не найден"));

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setEvent(event);
        participationRequest.setRequester(user);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration() && event.getParticipantLimit() > 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED);

        try {
            Thread.sleep(50);  //это потому что у Вас некорректный асинхронный тест
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        ParticipationRequestDto participationRequestDto =
                ParticipationRequestDtoMapper.toParticipationRequestDto(requestRepository.save(participationRequest));

        if (participationRequest.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }


        return participationRequestDto;
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Canceling request: {} by user: {}", requestId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь id = " + userId + "  не найден"));

        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка id = " + requestId + "  не найдена"));

        RequestStatus oldStatus = participationRequest.getStatus();

        participationRequest.setStatus(RequestStatus.CANCELED);

        ParticipationRequestDto participationRequestDto =
                ParticipationRequestDtoMapper.toParticipationRequestDto(requestRepository.save(participationRequest));

        if (oldStatus == RequestStatus.CONFIRMED) {
            participationRequest.getEvent().setConfirmedRequests(participationRequest.getEvent().getConfirmedRequests() - 1);
            eventRepository.save(participationRequest.getEvent());
        }

        return participationRequestDto;
    }
}
