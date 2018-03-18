package eu.immontilla.ryanair.client.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;

@Service
public class ScheduleFinderServiceImpl implements ScheduleFinderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleFinderServiceImpl.class);

    @Value("${url.api.schedules}")
    String urlApiSchedule;

    @Override
    public Schedule get(String from, String to, LocalDateTime dateTime) {
        URI uri = null;
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        StringBuilder fullURL = new StringBuilder().append(urlApiSchedule).append("/").append(from).append("/")
                .append(to).append("/years/").append(year).append("/months/").append(month);
        try {
            uri = new URI(fullURL.toString());
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Schedule> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Schedule>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info(String.format("Fligths from %s to %s on %d/%d has been found!", from, to, month, year));
                return response.getBody();
            }
            return null;
        } catch (URISyntaxException e) {
            LOGGER.error(String.format("URISyntaxException: %s", e.getMessage()));
            return null;
        }
    }

}
