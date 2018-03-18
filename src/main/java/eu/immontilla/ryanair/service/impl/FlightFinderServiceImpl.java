package eu.immontilla.ryanair.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Collection<Flight> flightsAvailables = Collections.emptyList();

        boolean routeAvailable = getDirectRouteAvailable(routes, departure, arrival);

        if (routeAvailable) {
            LOGGER.info(String.format("Direct route between %s and %s available!", departure, arrival));
            flightsAvailables = getFlightsAvailables(departure, arrival, departureDateTime);
        } else {
            LOGGER.info(String.format("No direct route between %s and %s has been found.", departure, arrival));
        }

        if (!flightsAvailables.isEmpty()) {
            return getFlightResults(flightsAvailables, departure, arrival, departureDateTime);
        }

        return Collections.emptyList();
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
    private Collection<Flight> getFlightsAvailables(String from, String to, LocalDateTime dateTime) {
        Schedule schedule = scheduleFinderService.get(from, to, dateTime);
        if (schedule != null) {
            return getFlightsOnSchedule(schedule, from, to, dateTime);
        }
        return Collections.emptyList();
    }

    /**
     * Condition to look for flights on a specific date
     * 
     * @param dateTime
     * @return
     */
    private Predicate<Flight> fligthsOnDateTime(LocalDateTime dateTime) {
        return p -> p.getDepartureTime().equalsIgnoreCase(dateTime.toString());
    }

    /**
     * List of scheduled flights on a specific date
     * 
     * @param schedule
     * @param from
     * @param to
     * @param dateTime
     * @return
     */
    private Collection<Flight> getFlightsOnSchedule(Schedule schedule, String from, String to, LocalDateTime dateTime) {

        Collection<Flight> availablesFlights = getAvailablesFlights(schedule, dateTime);
        LOGGER.info(String.format("availablesFlights: %s", availablesFlights.toString()));

        Collection<Flight> flightsOnSchedule = availablesFlights.stream().filter(fligthsOnDateTime(dateTime))
                .collect(Collectors.toList());
        LOGGER.info(String.format("flightsOnSchedule: %s", flightsOnSchedule.toString()));

        return flightsOnSchedule;
    }
    
    /**
     * Condition to get scheduled flights on a day
     * @param day
     * @return
     */
    private Predicate<DayFlight> flightsOnDay(int day) {
        return p -> (p.getDay() == day);
    }
    
    /**
     * List of avalaibles flights on a specific date
     * @param schedule
     * @param dateTime
     * @return
     */
    private Collection<Flight> getAvailablesFlights(Schedule schedule, LocalDateTime dateTime) {
        int day = dateTime.getDayOfMonth();

        Collection<Flight> flights = schedule.getDays().stream().filter(flightsOnDay(day)).collect(Collectors.toList())
                .get(0).getFlights();

        Collection<Flight> result = flights.stream().map(temp -> {
            Flight obj = new Flight();
            obj.setNumber(temp.getNumber());
            obj.setDepartureTime(getFullDateTime(dateTime, temp.getDepartureTime()));
            obj.setArrivalTime(getFullDateTime(dateTime, temp.getArrivalTime()));
            return obj;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * Date time transformation
     * 
     * @param dateTime
     * @param hourMinute
     * @return
     */
    private String getFullDateTime(LocalDateTime dateTime, String hourMinute) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = Integer.valueOf(hourMinute.split(":")[0]);
        int minute = Integer.valueOf(hourMinute.split(":")[1]);
        return LocalDateTime.of(year, month, day, hour, minute, 0).toString();
    }

    /**
     * Avalaibles flights mapped as a list of FlightResult
     * 
     * @param flights
     * @param departure
     * @param arrival
     * @param dateTime
     * @return
     */
    private List<FlightResult> getFlightResults(Collection<Flight> flights, String departure, String arrival,
            LocalDateTime dateTime) {
        List<FlightResult> flightResults = new ArrayList<FlightResult>();
        FlightResult flightResult = new FlightResult();
        flightResult.setStops(0);
        Leg leg = new Leg();
        for (Flight flight : flights) {
            leg.setDepartureAirport(departure);
            leg.setArrivalAirport(arrival);
            leg.setDepartureDateTime(flight.getDepartureTime());
            leg.setArrivalDateTime(flight.getArrivalTime());
        }
        List<Leg> legs = new ArrayList<Leg>();
        legs.add(leg);
        flightResult.setLegs(legs);
        flightResults.add(flightResult);
        return flightResults;
    }

}
