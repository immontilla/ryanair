package eu.immontilla.ryanair.service.impl.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.immontilla.ryanair.client.model.Route;
import eu.immontilla.ryanair.client.model.Stop;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.model.Leg;

public class NonDirectConnections {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonDirectConnections.class);
    private static final NonDirectConnections INSTANCE = new NonDirectConnections();

    private NonDirectConnections() {
    }

    public static NonDirectConnections getInstance() {
        return INSTANCE;
    }

    /**
     * Calculate non-direct routes from departure to arrival
     * 
     * @param routes
     * @param departure
     * @param arrival
     * @return
     */
    public List<Stop> getNonDirectRoutes(List<Route> routes, String departure, String arrival) {
        List<Route> arriveTo = getArriveTo(routes, arrival);
        List<Route> departFrom = getDepartFrom(routes, departure);
        List<Route> alternatives = new ArrayList<Route>();
        List<Stop> stops = new ArrayList<Stop>();
        String airportFrom;
        for (Route arrivesTo : arriveTo) {
            airportFrom = arrivesTo.getAirportFrom();
            alternatives = departFrom.stream().filter(arriveTo(airportFrom)).collect(Collectors.toList());
            for (Route departsFrom : alternatives) {
                stops.add(new Stop(departsFrom, arrivesTo));
            }
        }
        return stops;
    }

    /**
     * Condition to check the arrival airport of a list of Routes
     * 
     * @param routes
     * @param airport
     * @return
     */
    private List<Route> getArriveTo(List<Route> routes, String airport) {
        return routes.stream().filter(arriveTo(airport)).collect(Collectors.toList());
    }

    /**
     * Condition to check the arrival airport of a Route
     * 
     * @param airport
     * @return
     */
    private Predicate<Route> arriveTo(String airport) {
        return p -> p.getAirportTo().equalsIgnoreCase(airport);
    }

    /**
     * Condition to check the departure airport of a list of Routes
     * 
     * @param airport
     * @return
     */
    private List<Route> getDepartFrom(List<Route> routes, String airport) {
        return routes.stream().filter(departFrom(airport)).collect(Collectors.toList());
    }

    /**
     * Condition to check the departure airport of a Route
     * 
     * @param airport
     * @return
     */
    private Predicate<Route> departFrom(String airport) {
        return p -> p.getAirportFrom().equalsIgnoreCase(airport);
    }

    /**
     * Condition to check if I can go to airport from another Leg
     * 
     * @param airport
     * @return
     */
    private Predicate<FlightResult> departFromStop(String airport) {
        return p -> p.getLegs().get(0).getDepartureAirport().equalsIgnoreCase(airport);
    }

    /**
     * Flights avaliables from one-stop connections
     * 
     * @param scheduleFinder
     * @param stops
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<FlightResult> getFlightsAvailables(ScheduleFinderService scheduleFinder, List<Stop> stops,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Helper helper = Helper.getInstance();

        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();

        List<FlightResult> flightsToTheStops = new ArrayList<FlightResult>();
        List<FlightResult> flightsFromTheStops = new ArrayList<FlightResult>();
        String from = "", to = "";
        for (Stop stop : stops) {
            from = stop.getTo().getAirportFrom();
            to = stop.getTo().getAirportTo();
            flightsFromTheStops
                    .addAll(helper.getFlightsAvailables(scheduleFinder, from, to, startDateTime, endDateTime));
            from = stop.getFrom().getAirportFrom();
            to = stop.getFrom().getAirportTo();
            flightsToTheStops.addAll(helper.getFlightsAvailables(scheduleFinder, from, to, startDateTime, endDateTime));
        }

        Leg candidate;
        String arrivalAt, arrivalAirport, departureAirport;
        LocalDateTime minDepartureFromStop, departureDateTime, arrivalDateTime;
        List<FlightResult> flightsFromTheStop = new ArrayList<FlightResult>();
        for (FlightResult flightTo : flightsToTheStops) {
            departureAirport = flightTo.getLegs().get(0).getDepartureAirport();
            arrivalAirport = flightTo.getLegs().get(0).getArrivalAirport();
            arrivalAt = flightTo.getLegs().get(0).getArrivalDateTime();
            minDepartureFromStop = LocalDateTime.parse(arrivalAt).plusHours(2);
            LOGGER.info(String.format("Looking for flights from %s to %s departing after %s ...", departureAirport,
                    arrivalAirport, minDepartureFromStop.toString()));
            flightsFromTheStop = flightsFromTheStops.stream().filter(departFromStop(arrivalAirport))
                    .collect(Collectors.toList());
            for (FlightResult flightFrom : flightsFromTheStop) {
                candidate = flightFrom.getLegs().get(0);
                departureDateTime = LocalDateTime.parse(candidate.getDepartureDateTime());
                arrivalDateTime = LocalDateTime.parse(candidate.getArrivalDateTime());
                if (departureDateTime.isAfter(minDepartureFromStop)) {
                    LOGGER.info(String.format("%s at %s would be OK.", candidate.getDepartureAirport(), departureDateTime));
                    if (helper.validFlight(startDateTime, endDateTime, departureDateTime, arrivalDateTime)) {
                        flightsAvailables.add(helper.createFlightResult(flightTo.getLegs().get(0), candidate));
                    }
                }
            }
        }

        return flightsAvailables;
    }

}
