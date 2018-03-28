package eu.immontilla.ryanair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import eu.immontilla.ryanair.model.Leg;
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
    static class FlightFinderServiceImplTestContextConfiguration {
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
        routes.add(new Route(MAD, DUB, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(DUB, LIS, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(LGW, DUB, null, false, false, RYANAIR, ANYTHING));
        routes.add(new Route(DUB, MAD, null, false, false, RYANAIR, ANYTHING));
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
        assertEquals(0, flightResults.get(0).getStops());
    }

    @Test
    public void whenLookForFlightInRouteWithScheduleNextYearThenReturnScheduleList() {
        int day = 28;
        int month = 12;
        int year = now.getYear();
        LocalDateTime december28th = LocalDateTime.of(year, month, day, 6, 0);
        String departureDateTime = december28th.plusMinutes(30)
                .format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        String arrivalDateTime = december28th.plusHours(4).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        List<DayFlight> days = new ArrayList<DayFlight>();
        List<Flight> flights = new ArrayList<Flight>();
        flights.add(new Flight("7177", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule schedule = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(DUB, MAD, 12, year)).thenReturn(schedule);
        month = 1;
        days = new ArrayList<DayFlight>();
        flights = new ArrayList<Flight>();
        departureDateTime = december28th.plusDays(4).plusMinutes(30)
                .format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        arrivalDateTime = december28th.plusDays(4).plusHours(4)
                .format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        flights.add(new Flight("7277", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule scheduleNextYearJanuary = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(DUB, MAD, month, year + 1)).thenReturn(scheduleNextYearJanuary);
        List<FlightResult> flightResults = flightFinderService.findFlights(DUB, MAD, december28th,
                december28th.plusDays(5));
        assertEquals(0, flightResults.get(0).getStops());
    }

    @Test
    public void whenLookForFlightNoDirectOneStopAvailableNoScheduleThenReturnEmptyList() {
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        Mockito.when(scheduleFinderService.get(MAD, DUB, month, year)).thenReturn(null);
        List<FlightResult> flightResults = flightFinderService.findFlights(MAD, LIS, tomorrow, afterTomorrow);
        assertTrue(flightResults.isEmpty());
    }

    @Test
    public void whenLookForFlightNoDirectOneStopAvailableWithScheduleThenReturnFlightResultList() {
        int day = tomorrow.getDayOfMonth();
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        String departureDateTime = tomorrow.plusMinutes(30).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        String arrivalDateTime = tomorrow.plusHours(4).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        List<DayFlight> days = new ArrayList<DayFlight>();
        List<Flight> flights = new ArrayList<Flight>();
        flights.add(new Flight("7177", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule scheduleMADDUB = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(MAD, DUB, month, year)).thenReturn(scheduleMADDUB);
        days = new ArrayList<DayFlight>();
        flights = new ArrayList<Flight>();
        LocalDateTime connection = tomorrow.plusHours(7);
        day = connection.getDayOfMonth();
        month = connection.getMonthValue();
        year = connection.getYear();
        departureDateTime = connection.format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        arrivalDateTime = connection.plusHours(3).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        flights.add(new Flight("7889", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule scheduleDUBLIS = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(DUB, LIS, month, year)).thenReturn(scheduleDUBLIS);
        List<FlightResult> flightResults = flightFinderService.findFlights(MAD, LIS, tomorrow, afterTomorrow);
        assertEquals(1, flightResults.get(0).getStops());
    }

    @Test
    public void whenLookForFlightNoDirectOneStopNoScheduleAvailableThenReturnFlightResultList() {
        int day = tomorrow.getDayOfMonth();
        int month = tomorrow.getMonthValue();
        int year = tomorrow.getYear();
        String departureDateTime = tomorrow.plusMinutes(30).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        String arrivalDateTime = tomorrow.plusHours(4).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        List<DayFlight> days = new ArrayList<DayFlight>();
        List<Flight> flights = new ArrayList<Flight>();
        flights.add(new Flight("7177", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule scheduleMADDUB = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(MAD, DUB, month, year)).thenReturn(scheduleMADDUB);
        days = new ArrayList<DayFlight>();
        flights = new ArrayList<Flight>();
        LocalDateTime connection = tomorrow.plusHours(7);
        day = connection.getDayOfMonth();
        month = connection.getMonthValue();
        year = connection.getYear();
        departureDateTime = connection.format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        arrivalDateTime = connection.plusHours(3).format(DateTimeFormatter.ofPattern(ONLY_HOUR_AND_MINUTE));
        flights.add(new Flight("7889", departureDateTime, arrivalDateTime));
        days.add(new DayFlight(day, flights));
        Schedule scheduleDUBLIS = new Schedule(month, days);
        Mockito.when(scheduleFinderService.get(DUB, LIS, month, year)).thenReturn(scheduleDUBLIS);
        List<FlightResult> flightResults = flightFinderService.findFlights(MAD, LIS, tomorrow, afterTomorrow);
        assertEquals(flightResults.get(0).getStops(), 1);
    }

    @Test
    public void modelFlightResultLegLogTest() {
        List<Leg> legs = new ArrayList<Leg>();
        Leg legOne = new Leg();
        legOne.setDepartureAirport(MAD);
        legOne.setArrivalAirport(DUB);
        legOne.setDepartureDateTime(now.toString());
        legOne.setArrivalDateTime(tomorrow.toString());
        legs.add(legOne);
        Leg legTwo = new Leg();
        legTwo.setDepartureAirport(MAD);
        legTwo.setArrivalAirport(DUB);
        legTwo.setDepartureDateTime(now.toString());
        legTwo.setArrivalDateTime(tomorrow.toString());
        legs.add(legTwo);
        FlightResult flightResult = new FlightResult(1, legs);
        assertNotNull(legOne.toString());
        assertNotNull(legTwo.toString());
        assertNotNull(flightResult.toString());
    }

}
