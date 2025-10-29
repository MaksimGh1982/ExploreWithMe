package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.common.EventStateActionUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {

    private EventStateActionUser stateAction; // SEND_TO_REVIEW, CANCEL_REVIEW
}
