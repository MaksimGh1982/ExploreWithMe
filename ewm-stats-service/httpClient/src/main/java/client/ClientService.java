package client;

import dto.EndpointHitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class ClientService extends BaseClient {

    @Autowired
    public ClientService(@Value("${stats_server_url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> hit(String app, String uri, String ip, LocalDateTime timestamp, Long eventId) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setIp(ip);
        endpointHitDto.setUri(uri);
        endpointHitDto.setApp(app);
        endpointHitDto.setTimestamp(timestamp);
        endpointHitDto.setEventId(eventId);
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String uris, boolean unique) {
        log.info("HttpClient GetStats" + "start = " + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                " end = " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " uris = " + uris +
                " unique = " + unique);

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", -1L,
                Map.of(
                        "start", start,
                        "end", end,
                        "uris", uris,
                        "unique", String.valueOf(unique)));

    }

    public ResponseEntity<Object> getViews(Long eventId) {
        log.info("HttpClient getViews eventId=" + eventId);
        return get("/stats/" + eventId, -1L, null);
    }
}
