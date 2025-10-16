package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Long confirmedRequests;

    @NotBlank
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    private Long views;
}

