package eu.immontilla.ryanair.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.service.FlightFinderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST Controller
 * 
 * @author immontilla
 */
@RestController
@Api(value = "api")
@RequestMapping("/api")
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private FlightFinderService flightFinderService;

    @ApiOperation(value = "A list of flights departing from a given departure airport not earlier than the specified departure datetime and arriving to a given arrival airport not later than the specified arrival datetime. For interconnected flights the difference between the arrival and the next departure should be 2h or greater", produces = MediaType.APPLICATION_JSON_VALUE, httpMethod = "GET")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success!", response = FlightResult[].class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 405, message = "Method not allowed") })
    @RequestMapping(value = "/interconnections", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FlightResult>> flightResults(
            @ApiParam(value = "A departure airport IATA code like DUB", required = true) @RequestParam("departure") String departure,
            @ApiParam(value = "An arrival airport IATA code like BCN", required = true) @RequestParam("arrival") String arrival,
            @ApiParam(value = "A departure datetime in the departure airport timezone in ISO format like 2018-06-01T07:00", required = true) @RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
            @ApiParam(value = "An arrival datetime in the arrival airport timezone in ISO format like 2018-06-02T15:00", required = true) @RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {
        LOGGER.info(String.format("Searching flights from %s to %s - Departure %s - Arrival %s", departure, arrival,
                departureDateTime, arrivalDateTime));
        List<FlightResult> flightResults = flightFinderService.findFlights(departure, arrival, departureDateTime,
                arrivalDateTime);
        return ResponseEntity.status(HttpStatus.OK).body(flightResults);
    }

}
