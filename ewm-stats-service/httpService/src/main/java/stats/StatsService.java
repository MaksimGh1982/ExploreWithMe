package stats;

import dto.EndpointHitDto;
import dto.StatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatsService {
    private final EndpointHitRepository endpointHitRepository;

    @Autowired
    public StatsService(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        log.info("Создать запись статистики uri = " + endpointHitDto.getUri());
        endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    public Collection<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("GetStats" + "start = " + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                " end = " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " uris = " + uris.toString() +
                " unique = " + String.valueOf(unique));

        List<StatsDto> statsDto;
        if (unique) {
            statsDto = endpointHitRepository.uniqueGetStatsDto(start, end);
        } else {
            statsDto = endpointHitRepository.notUniqueGetStatsDto(start, end);
        }

        if (uris.size() == 0) {
            return statsDto;
        } else {
            return statsDto.stream()
                    .filter(item -> uris.contains(item.getUri()))
                    .collect(Collectors.toList());
        }
    }
}
