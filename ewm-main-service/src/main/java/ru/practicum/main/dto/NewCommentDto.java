package ru.practicum.main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCommentDto {

    @NotBlank
    private String content;
}
