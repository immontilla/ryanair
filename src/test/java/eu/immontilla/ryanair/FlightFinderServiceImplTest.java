package eu.immontilla.ryanair;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import eu.immontilla.ryanair.client.model.DayFlight;
import eu.immontilla.ryanair.client.model.Flight;
import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.AvailableRouteService;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.service.FlightFinderService;
import eu.immontilla.ryanair.service.impl.FlightFinderServiceImpl;

@RunWith(SpringRunner.class)
public class FlightFinderServiceImplTest {
    private static final String CRL = "CRL";
    private static final String LGW = "LGW";
    private static final String MAD = "MAD";
    private static final String DUB = "DUB";
    private static final String LIS = "LIS";
    private static final String CLO = "CLO";
    private static final String RYANAIR = "RYANAIR";
    private static final String ANYTHING = "ANYTHING";
    private static final String ONLY_HOUR_AND_MINUTE = "HH:mm";

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public FlightFinderService flightFinderService() {
            return new FlightFinderServiceImpl();
        }
    }

    @Autowired
    private FlightFinderService flightFinderService;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tomorrow = now.plusDays(1);
    LocalDateTime afterTomorrow = now.plusDays(2);

    @MockBean
    private AvailableRouteService availableRouteService;
    @MockBean
    private ScheduleFinderService scheduleFinderService;

    @Before
    public void setUp() {
        List<Route> routes = new ArrayList<Route>();
        routes.add(new Route(CRL, DUB, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(CRL, LIS, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(DUB, MAD, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(LGW, DUB, null, false, false, RYANAIR, ANYTHING));
        Mockito.when(availableRouteService.getAll()).thenReturn(routes);
    }

    @Test
    public void whenLookForFlightNotInRouteThenReturnEmptyList() {
        List<FlightResult> flights = flightFinderService.findFlights(MAD, CLO, tomorrow, afterTomorrow);
        assertTrue(flights.isEmpty());
    }

    @Test
    public void whenLookForFlightInRouteWithNoScheduleThenReturnEmptyList() {
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        Mockito.when(scheduleFinderService.get(DUB, MAD, month, year)).thenReturn(null);
        List<FlightResult> flightResults = flightFinderService.findFlights(DUB, MAD, tomorrow, afterTomorrow);
        assertTrue(flightResults.isEmpty());
    }

    @Test
    public void whenLookForFlightInRouteWithScheduleThenReturnScheduleList() {
        int day = tomorrow.getDayOfMonth();
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        String departureDateTime = tomorrow.plusMinutes(30).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        String arrivalDateTime = tomorrow.plusHours(4).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        List<DayFlight> days = new ArrayList<DayFlight>();
        List<Flight> flights = new ArrayList<Flight>();
        flights.add(new Flight("7177", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule schedule = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(DUB, MAD, month, year)).thenReturn(schedule);
        List<FlightResult> flightResults = flightFinderService.findFlights(DUB, MAD, tomorrow, afterTomorrow);
        assertTrue(!flightResults.isEmpty());
        assertTrue(flightResults.size() == 1);
    }

}
