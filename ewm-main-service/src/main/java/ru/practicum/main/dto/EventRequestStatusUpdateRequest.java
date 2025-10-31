package ru.practicum.main.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.common.RequestStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotNull
    private List<Long> requestIds;

    @NotNull
    private RequestStatus status; // CONFIRMED, REJECTED
}
