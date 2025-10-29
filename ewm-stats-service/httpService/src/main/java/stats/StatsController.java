package stats;

import dto.EndpointHitDto;
import dto.StatsDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@Validated
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto save(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return statsService.hit(endpointHitDto);
    }

    @GetMapping("/stats")
    public Collection<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @RequestParam(defaultValue = "") List<String> uris,
                                         @RequestParam(defaultValue = "false") boolean unique) {

        return statsService.getStats(start, end, uris, unique);
    }

    @GetMapping("/stats/{eventId}")
    public Integer getViews(@PathVariable Long eventId) {
        return statsService.getViews(eventId);

    }
}
