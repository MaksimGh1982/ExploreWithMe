package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.model.ParticipationRequest;

@UtilityClass
public class ParticipationRequestDtoMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(participationRequest.getId(),
                participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus().toString());
    }
}
