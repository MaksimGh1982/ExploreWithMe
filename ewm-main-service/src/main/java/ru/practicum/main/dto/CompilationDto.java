package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;

    private Boolean pinned;

    @NotBlank
    private String title;

    private List<EventShortDto> events;
}
