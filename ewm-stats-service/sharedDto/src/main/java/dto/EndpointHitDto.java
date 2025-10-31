package dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;
    @NotBlank(message = "Приложение должно быть указано")
    private String app;
    @NotBlank(message = "Uri должно быть указано")
    private String uri;
    @NotBlank(message = "Ip должно быть указано")
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    Long eventId;

}



