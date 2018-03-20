package eu.immontilla.ryanair.service.impl.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import eu.immontilla.ryanair.client.model.DayFlight;
import eu.immontilla.ryanair.client.model.Flight;
import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;

public class DirectConnections {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectConnections.class);
    private static final DirectConnections INSTANCE = new DirectConnections();

    private DirectConnections() {
    }

    public static DirectConnections getInstance() {
        return INSTANCE;
    }

    /**
     * Check if exists a direct route
     * 
     * @param routes
     * @param from
     * @param to
     * @return
     */
    public boolean getDirectRouteAvailable(List<Route> routes, String from, String to) {
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
    public List<FlightResult> getFlightsAvailables(ScheduleFinderService scheduleFinderService, String from, String to,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Helper helper = Helper.getInstance();
        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();
        int day = 0;
        int month = 0;
        int year = startDateTime.getYear();
        LocalDateTime departureDateTime, arrivalDateTime;
        FlightResult flightResult;
        List<Schedule> schedules = helper.getSchedules(scheduleFinderService, from, to, startDateTime, endDateTime);
        if (!schedules.isEmpty()) {
            LOGGER.info(String.format("Scheduled flights: %s", Joiner.on(" + ").join(schedules)));
            for (Schedule schedule : schedules) {
                month = schedule.getMonth();
                for (DayFlight dayFlight : schedule.getDays()) {
                    day = dayFlight.getDay();
                    for (Flight flight : dayFlight.getFlights()) {
                        departureDateTime = helper.createLocalDateTime(year, month, day, flight.getDepartureTime());
                        arrivalDateTime = helper.createLocalDateTime(year, month, day, flight.getArrivalTime());
                        if (helper.validFlight(startDateTime, endDateTime, departureDateTime, arrivalDateTime)) {
                            flightResult = helper.createFlightResult(from, to, departureDateTime, arrivalDateTime);
                            flightsAvailables.add(flightResult);
                        }
                    }
                }
            }
        }
        return flightsAvailables;
    }

}
