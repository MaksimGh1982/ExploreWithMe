package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;

    private String created;

    private Long event;

    private Long requester;

    private String status; // PENDING, CONFIRMED, REJECTED, CANCELED
}
