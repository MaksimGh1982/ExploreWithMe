package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.common.CommentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    private EventShortDto event;
    @NotNull
    private UserShortDto user;
    @NotBlank
    private String content;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private CommentStatus status;

}
