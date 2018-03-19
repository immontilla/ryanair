package eu.immontilla.ryanair.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import eu.immontilla.ryanair.client.model.DayFlight;
import eu.immontilla.ryanair.client.model.Flight;
import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.AvailableRouteService;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.model.Leg;
import eu.immontilla.ryanair.service.FlightFinderService;

@Service
public class FlightFinderServiceImpl implements FlightFinderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlightFinderServiceImpl.class);

    @Autowired
    private AvailableRouteService availableRouteService;

    @Autowired
    private ScheduleFinderService scheduleFinderService;

    @Override
    public List<FlightResult> findFlights(String departure, String arrival, LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {

        if (arrivalDateTime.isBefore(departureDateTime)) {
            return Collections.emptyList();
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        if (departureDateTime.isBefore(localDateTime)) {
            return Collections.emptyList();
        }

        if (departureDateTime.plusHours(2L).isAfter(arrivalDateTime)) {
            return Collections.emptyList();
        }

        List<Route> routes = availableRouteService.getAll();
        LOGGER.info(String.format("%d direct routes has been found!", routes.size()));

        List<FlightResult> flightResults = getAvailablesFlights(routes, departure, arrival, departureDateTime,
                arrivalDateTime);

        LOGGER.info(String.format("Flight results: %s", Joiner.on(" + ").join(flightResults)));

        return flightResults;
    }

    /**
     * Return a list of availables flights
     * 
     * @param routes
     * @param departure
     * @param arrival
     * @param departureDateTime
     * @param arrivalDateTime
     * @return
     */
    private List<FlightResult> getAvailablesFlights(List<Route> routes, String departure, String arrival,
            LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        List<FlightResult> flightsAvailables = Collections.emptyList();

        boolean routeAvailable = getDirectRouteAvailable(routes, departure, arrival);

        if (routeAvailable) {
            LOGGER.info(String.format("Direct route between %s and %s available!", departure, arrival));
            flightsAvailables = getFlightsAvailables(departure, arrival, departureDateTime, arrivalDateTime);
        } else {
            LOGGER.info(String.format("No direct route between %s and %s has been found.", departure, arrival));
        }

        return flightsAvailables;
    }

    /**
     * Check if exists a direct route
     * 
     * @param routes
     * @param from
     * @param to
     * @return
     */
    private boolean getDirectRouteAvailable(List<Route> routes, String from, String to) {
        return routes.stream().anyMatch(directRoute(from, to));
    }

    /**
     * Condition for a direct connection between two airports
     * 
     * @param from
     * @param to
     * @return
     */
    private Predicate<Route> directRoute(String from, String to) {
        return p -> p.getAirportFrom().equalsIgnoreCase(from) && p.getAirportTo().equalsIgnoreCase(to);
    }

    /**
     * Return a list of availables flights
     * 
     * @param from
     * @param to
     * @param dateTime
     * @return
     */
    private List<FlightResult> getFlightsAvailables(String from, String to, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();
        int day = 0;
        int month = 0;
        int year = startDateTime.getYear();
        LocalDateTime departureDateTime, arrivalDateTime;
        FlightResult flightResult;
        List<Schedule> schedules = getSchedules(from, to, startDateTime, endDateTime);
        if (!schedules.isEmpty()) {
            LOGGER.info(String.format("Scheduled flights: %s", Joiner.on(" + ").join(schedules)));
            for (Schedule schedule : schedules) {
                month = schedule.getMonth();
                for (DayFlight dayFlight : schedule.getDays()) {
                    day = dayFlight.getDay();
                    for (Flight flight : dayFlight.getFlights()) {
                        departureDateTime = createLocalDateTime(year, month, day, flight.getDepartureTime());
                        arrivalDateTime = createLocalDateTime(year, month, day, flight.getArrivalTime());
                        if (validFlight(startDateTime, endDateTime, departureDateTime, arrivalDateTime)) {
                            flightResult = createFlightResult(from, to, departureDateTime, arrivalDateTime);
                            flightsAvailables.add(flightResult);
                        }
                    }
                }
            }
        }
        return flightsAvailables;
    }

    /**
     * Return a LocalDateTime
     * 
     * @param year
     * @param month
     * @param day
     * @param time
     * @return
     */
    private LocalDateTime createLocalDateTime(int year, int month, int day, String time) {
        int hour = Integer.valueOf(time.split(":")[0]);
        int minute = Integer.valueOf(time.split(":")[1]);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    /**
     * Check if this is a valid flight
     * 
     * @param startDateTime
     * @param endDateTime
     * @param departureDateTime
     * @param arrivalDateTime
     * @return
     */
    private boolean validFlight(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {
        if (departureDateTime.isBefore(startDateTime)) {
            return false;
        }
        if (arrivalDateTime.isAfter(endDateTime)) {
            return false;
        }
        return true;
    }

    /**
     * Create a flight result
     * 
     * @param from
     * @param to
     * @param departureDateTime
     * @param arrivalDateTime
     * @return
     */
    private FlightResult createFlightResult(String from, String to, LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {
        FlightResult flightResult = new FlightResult();
        flightResult.setStops(0);
        List<Leg> legs = new ArrayList<Leg>();
        legs.add(new Leg(from, to, departureDateTime.toString(), arrivalDateTime.toString()));
        flightResult.setLegs(legs);
        return flightResult;
    }

    /**
     * Scheduled flights list
     * 
     * @param from
     * @param to
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    private List<Schedule> getSchedules(String from, String to, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        List<Schedule> schedules = new ArrayList<Schedule>();
        Schedule schedule = null;
        int startYear = startDateTime.getYear();
        int startMonth = startDateTime.getMonthValue();
        int endYear = endDateTime.getYear();
        int endMonth = endDateTime.getMonthValue();
        int month = startMonth;
        int year = startYear;
        if (endYear > startYear) {
            LocalDate endDate = LocalDate.of(endYear, Month.of(endMonth), endDateTime.getDayOfMonth()), date;
            while ((year <= endYear)) {
                schedule = scheduleFinderService.get(from, to, month, year);
                if (schedule != null) {
                    schedules.add(schedule);
                }
                month++;
                if (month > 12) {
                    month = 1;
                    year++;
                }
                date = LocalDate.of(year, Month.of(month), endDateTime.getDayOfMonth());
                if (date.isAfter(endDate)) {
                    break;
                }
            }
        } else {
            for (month = startMonth; month <= endMonth; month++) {
                schedule = scheduleFinderService.get(from, to, month, startYear);
                if (schedule != null) {
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

}
