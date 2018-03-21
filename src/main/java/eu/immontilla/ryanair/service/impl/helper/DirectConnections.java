package eu.immontilla.ryanair.service.impl.helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;

public class DirectConnections {
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
        List<FlightResult> flightsAvailables = Helper.getInstance().getFlightsAvailables(scheduleFinderService, from,
                to, startDateTime, endDateTime);
        return flightsAvailables;
    }

}
