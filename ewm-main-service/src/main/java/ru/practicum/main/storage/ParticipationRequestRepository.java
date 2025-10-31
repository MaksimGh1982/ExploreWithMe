package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.common.RequestStatus;
import ru.practicum.main.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    ParticipationRequest findByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findByRequesterId(Long userId);

    List<ParticipationRequest> findByEventId(Long id);

    List<ParticipationRequest> findByIdInAndStatus(List<Long> ids, RequestStatus status);
}
