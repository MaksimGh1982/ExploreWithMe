package ru.practicum.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.storage.EventRepository;
import ru.practicum.main.storage.ParticipationRequestRepository;
import ru.practicum.main.storage.UserRepository;

import java.util.List;

@Service
@Slf4j
public class RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    RequestService(EventRepository eventRepository, UserRepository userRepository,
                   ParticipationRequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Getting requests for user: {}", userId);
        // TODO: найти все заявки пользователя
        return List.of();
    }

    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        log.info("Adding participation request for user: {} to event: {}", userId, eventId);
        // TODO: проверить возможность подачи заявки, создать запрос
        return null;
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Canceling request: {} by user: {}", requestId, userId);
        // TODO: найти заявку, проверить права, отменить
        return null;
    }
}
