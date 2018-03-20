package eu.immontilla.ryanair.service.impl.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.model.Stop;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;

public class NonDirectConnections {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonDirectConnections.class);
    private static final NonDirectConnections INSTANCE = new NonDirectConnections();

    private NonDirectConnections() {
    }

    public static NonDirectConnections getInstance() {
        return INSTANCE;
    }

    public List<Stop> getNonDirectRoutes(List<Route> routes, String departure, String arrival) {
        List<Route> arriveTo = getArriveTo(routes, arrival);
        List<Route> departFrom = getDepartFrom(routes, departure);
        List<Route> alternatives = new ArrayList<Route>();
        List<Stop> stops = new ArrayList<Stop>();
        String airportFrom;
        for (Route arrivesTo : arriveTo) {
            airportFrom = arrivesTo.getAirportFrom();
            alternatives = departFrom.stream().filter(goesTo(airportFrom)).collect(Collectors.toList());
            for (Route departsFrom : alternatives) {
                stops.add(new Stop(departsFrom, arrivesTo));
            }
        }
        return stops;
    }

    public List<FlightResult> getFlightsAvailables(ScheduleFinderService scheduleFinderService, List<Stop> stops,
            String from, String to, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Helper helper = Helper.getInstance();
        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();
        List<Schedule> schedulesOne, scheduleTwo;
        for (Stop stop : stops) {
            schedulesOne = helper.getSchedules(scheduleFinderService, stop.getFrom().getAirportFrom(),
                    stop.getFrom().getAirportTo(), startDateTime, endDateTime);
            LOGGER.info(String.format("schedulesOne flights: %s", Joiner.on(" + ").join(schedulesOne)));
            scheduleTwo = helper.getSchedules(scheduleFinderService, stop.getTo().getAirportFrom(),
                    stop.getTo().getAirportTo(), startDateTime, endDateTime);
            LOGGER.info(String.format("scheduleTwo flights: %s", Joiner.on(" + ").join(scheduleTwo)));
        }
        return flightsAvailables;
    }

    private List<Route> getArriveTo(List<Route> routes, String airport) {
        return routes.stream().filter(arriveTo(airport)).collect(Collectors.toList());
    }

    private Predicate<Route> arriveTo(String airport) {
        return p -> p.getAirportTo().equalsIgnoreCase(airport);
    }

    private List<Route> getDepartFrom(List<Route> routes, String airport) {
        return routes.stream().filter(departFrom(airport)).collect(Collectors.toList());
    }

    private Predicate<Route> departFrom(String airport) {
        return p -> p.getAirportFrom().equalsIgnoreCase(airport);
    }

    private Predicate<Route> goesTo(String airport) {
        return p -> p.getAirportTo().equalsIgnoreCase(airport);
    }

}
