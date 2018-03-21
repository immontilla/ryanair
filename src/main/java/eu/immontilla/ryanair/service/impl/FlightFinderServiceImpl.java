package eu.immontilla.ryanair.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Stop;
import eu.immontilla.ryanair.client.service.AvailableRouteService;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.service.FlightFinderService;
import eu.immontilla.ryanair.service.impl.helper.DirectConnections;
import eu.immontilla.ryanair.service.impl.helper.NonDirectConnections;

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

        List<Route> routes = availableRouteService.getAll();
        LOGGER.info(String.format("%d routes has been found!", routes.size()));

        List<FlightResult> flightResults = getDirectConnnectionFlights(routes, departure, arrival, departureDateTime,
                arrivalDateTime);
        flightResults
                .addAll(getNonDirectConnnectionFlights(routes, departure, arrival, departureDateTime, arrivalDateTime));

        LOGGER.info(String.format("%d flights has been found", flightResults.size()));

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
    private List<FlightResult> getDirectConnnectionFlights(List<Route> routes, String departure, String arrival,
            LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();

        DirectConnections directConnections = DirectConnections.getInstance();

        boolean routeAvailable = directConnections.getDirectRouteAvailable(routes, departure, arrival);

        if (routeAvailable) {
            LOGGER.info(String.format("Direct route between %s and %s available!", departure, arrival));
            flightsAvailables = directConnections.getFlightsAvailables(scheduleFinderService, departure, arrival,
                    departureDateTime, arrivalDateTime);
        } else {
            LOGGER.info(String.format("No direct route between %s and %s has been found.", departure, arrival));
        }

        return flightsAvailables;
    }

    private List<FlightResult> getNonDirectConnnectionFlights(List<Route> routes, String departure, String arrival,
            LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();

        NonDirectConnections nonDirectConnections = NonDirectConnections.getInstance();

        List<Stop> nonDirectRoutes = nonDirectConnections.getNonDirectRoutes(routes, departure, arrival);

        if (!nonDirectRoutes.isEmpty()) {
            LOGGER.info(String.format("One stop alternative routes between %s and %s availables!", departure, arrival));
            LOGGER.info(nonDirectRoutes.toString());
            flightsAvailables = nonDirectConnections.getFlightsAvailables(scheduleFinderService, nonDirectRoutes,
                    departureDateTime, arrivalDateTime);
        } else {
            LOGGER.info(String.format("No one-stop alternative route between %s and %s has been found.", departure,
                    arrival));
        }

        return flightsAvailables;
    }

}
