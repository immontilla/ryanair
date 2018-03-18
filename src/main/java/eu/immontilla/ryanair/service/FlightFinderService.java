package eu.immontilla.ryanair.service;

import java.time.LocalDateTime;
import java.util.List;

import eu.immontilla.ryanair.model.FlightResult;

/***
 * This service expose an operation to find fligths from departure to arrival on a range of dates
 * 
 * @author immontilla
 */
public interface FlightFinderService {
    List<FlightResult> findFlights(String departure, String arrival, LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime);
}
