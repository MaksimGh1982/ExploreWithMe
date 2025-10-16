package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.ParticipationRequest;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
}
