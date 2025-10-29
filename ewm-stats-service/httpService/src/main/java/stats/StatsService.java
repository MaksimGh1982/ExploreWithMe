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

    public EndpointHitDto hit(EndpointHitDto endpointHitDto) {
        log.info("Создать запись статистики uri = " + endpointHitDto.getUri());
        return EndpointHitMapper.endpointHitDto(endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    public Integer getViews(Long eventId) {
        log.info("Количество уникальных просмотров события id = " + eventId);
        return endpointHitRepository.countUniqueViewsByEventId(eventId);
    }

    public Collection<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("GetStats" + "start = " + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                " end = " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " uris = " + uris.toString() +
                " unique = " + String.valueOf(unique));

        if (end.isBefore(start)) {
            throw new ValidException("Дата начала диапазона должна предшествовать дате окончания");
        }

        List<StatsDto> statsDto;
        if (unique) {
            statsDto = endpointHitRepository.uniqueGetStatsDto(start, end);
        } else {
            statsDto = endpointHitRepository.notUniqueGetStatsDto(start, end);
        }

        if (uris.isEmpty()) {
            return statsDto;
        } else {
            return statsDto.stream()
                    .filter(item -> uris.contains(item.getUri()))
                    .collect(Collectors.toList());
        }
    }
}
