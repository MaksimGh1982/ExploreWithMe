package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.common.EventStateActionAdmin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private EventStateActionAdmin stateAction; // PUBLISH_EVENT, REJECT_EVENT
}
