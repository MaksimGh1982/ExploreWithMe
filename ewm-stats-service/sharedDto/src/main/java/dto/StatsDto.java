package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDto {
    @NotBlank(message = "Приложение должно быть указано")
    private String app;
    @NotBlank(message = "Uri должно быть указано")
    private String uri;
    @NotNull(message = "Статистика не должна быть пустой")
    private Long hits;
}
