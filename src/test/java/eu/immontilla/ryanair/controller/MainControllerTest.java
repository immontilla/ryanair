package eu.immontilla.ryanair.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import eu.immontilla.ryanair.service.FlightFinderService;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
    private static final String INTERCONNECTIONS = "/api/interconnections";
    private static final String DEPARTURE = "departure";
    private static final String ARRIVAL = "arrival";
    private static final String DEPARTUREDATETIME = "departureDateTime";
    private static final String ARRIVALDATETIME = "arrivalDateTime";
    private static final String ISO_DATE_TIME = "YYYY-MM-dd'T'hh:mm";
    private static final String MAD = "MAD";
    private static final String LIS = "LIS";
    private static final String DUB = "DUB";
    private static final String CLO = "CLO";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FlightFinderService service;

    LocalDateTime now = LocalDateTime.now();
    String today = now.format(DateTimeFormatter.ofPattern(ISO_DATE_TIME));
    String yesterday = now.plusDays(-1).format(DateTimeFormatter.ofPattern(ISO_DATE_TIME));
    String tomorrow = now.plusDays(1).format(DateTimeFormatter.ofPattern(ISO_DATE_TIME));
    String tomorrowPlusOneHour = now.plusDays(1).plusHours(1).format(DateTimeFormatter.ofPattern(ISO_DATE_TIME));
    String afterTomorrow = now.plusDays(2).format(DateTimeFormatter.ofPattern(ISO_DATE_TIME));

    @Test
    public void whenDepartureDateTimeIsYesterdayThenBadRequest() throws Exception {
        this.mvc.perform(get(INTERCONNECTIONS).contentType(MediaType.APPLICATION_JSON_VALUE).param(DEPARTURE, MAD)
                .param(ARRIVAL, LIS).param(DEPARTUREDATETIME, yesterday).param(ARRIVALDATETIME, tomorrow))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenArrivalDateTimeIsBeforeDepartureDateTimeThenBadRequest() throws Exception {
        this.mvc.perform(get(INTERCONNECTIONS).contentType(MediaType.APPLICATION_JSON_VALUE).param(DEPARTURE, MAD)
                .param(ARRIVAL, DUB).param(DEPARTUREDATETIME, tomorrow).param(ARRIVALDATETIME, today))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenArrivalDateTimeIsLessThan2HoursAfterDepartureDateTimeThenBadRequest() throws Exception {
        this.mvc.perform(get(INTERCONNECTIONS).contentType(MediaType.APPLICATION_JSON_VALUE).param(DEPARTURE, MAD)
                .param(ARRIVAL, DUB).param(DEPARTUREDATETIME, tomorrow).param(ARRIVALDATETIME, tomorrowPlusOneHour))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNotAvailableRouteThenNoContent() throws Exception {
        this.mvc.perform(get(INTERCONNECTIONS).contentType(MediaType.APPLICATION_JSON_VALUE).param(DEPARTURE, MAD)
                .param(ARRIVAL, CLO).param(DEPARTUREDATETIME, tomorrow).param(ARRIVALDATETIME, afterTomorrow))
                .andExpect(status().isNoContent());
    }
}
