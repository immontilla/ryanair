package eu.immontilla.ryanair.client.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;

@Service
@CacheConfig(cacheNames = "clientAPICache")
public class ScheduleFinderServiceImpl implements ScheduleFinderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleFinderServiceImpl.class);

    @Value("${url.api.schedules}")
    String urlApiSchedule;

    @Override
    @Cacheable
    public Schedule get(String from, String to, int month, int year) {
        URI uri = null;
        StringBuilder fullURL = new StringBuilder().append(urlApiSchedule).append("/").append(from).append("/")
                .append(to).append("/years/").append(year).append("/months/").append(month);        
        LOGGER.info(String.format("Looking for flights from %s to %s on %d/%d ...", from, to, month, year));
        try {
            uri = new URI(fullURL.toString());
            LOGGER.info(String.format("URL Request => %s", fullURL.toString()));
            return getSchedule(uri, from, to, month, year);
        } catch (URISyntaxException e) {
            LOGGER.error(String.format("URISyntaxException: %s", e.getMessage()));
        }
        return null;
    }

    private Schedule getSchedule(URI uri, String from, String to, int month, int year) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Schedule> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Schedule>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info(String.format("Flights from %s to %s on %d/%d has been found!", from, to, month, year));
                return response.getBody();
            }
        } catch (final HttpClientErrorException e) {
            LOGGER.error(String.format("%s: %s", e.getStatusCode(), e.getResponseBodyAsString()));
        }
        return null;
    }

}
