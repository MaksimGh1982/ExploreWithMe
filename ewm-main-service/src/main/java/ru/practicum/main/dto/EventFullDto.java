package ru.practicum.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.common.EventState;
import ru.practicum.main.common.GlobalConstant;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = GlobalConstant.DATA_PATTERN)
    private LocalDateTime createdOn;

    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank
    @JsonFormat(pattern = GlobalConstant.DATA_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private Integer participantLimit = 0;

    @JsonFormat(pattern = GlobalConstant.DATA_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration = true;

    private EventState state;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    private Integer views;
}
