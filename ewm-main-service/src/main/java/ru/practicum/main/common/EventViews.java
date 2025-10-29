package ru.practicum.main.common;

import client.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventViews {
    private final ClientService clientService;

    @Autowired
    EventViews(RestTemplateBuilder builder, @Value("${stats-server.url}") String serverUrl) {
        this.clientService = new ClientService(serverUrl, builder);
    }

    public Integer getViews(Long eventId) {
        log.info("getViews id=" + eventId);
        ResponseEntity<Object> response = clientService.getViews(eventId);
        Object body = response.getBody();
        if (body instanceof Integer) {
            return (Integer) body;
        } else {
            throw new IllegalStateException("Unexpected response body type: " + (body != null ? body.getClass() : "null"));
        }
    }
}
