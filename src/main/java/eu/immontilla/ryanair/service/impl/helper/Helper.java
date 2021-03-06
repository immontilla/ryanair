package eu.immontilla.ryanair.service.impl.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import eu.immontilla.ryanair.client.model.DayFlight;
import eu.immontilla.ryanair.client.model.Flight;
import eu.immontilla.ryanair.client.model.Schedule;
import eu.immontilla.ryanair.client.service.ScheduleFinderService;
import eu.immontilla.ryanair.model.FlightResult;
import eu.immontilla.ryanair.model.Leg;

public class Helper {
    private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);
    private static final Helper INSTANCE = new Helper();

    private Helper() {
    }

    public static Helper getInstance() {
        return INSTANCE;
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
        List<FlightResult> flightsAvailables = new ArrayList<FlightResult>();
        int day = 0;
        int month = 0;
        int year = startDateTime.getYear();
        LocalDateTime departureDateTime, arrivalDateTime;
        FlightResult flightResult;
        List<Schedule> schedules = getSchedules(scheduleFinderService, from, to, startDateTime, endDateTime);
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
     * Scheduled flights list
     * 
     * @param from
     * @param to
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<Schedule> getSchedules(ScheduleFinderService scheduleFinderService, String from, String to,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {
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

    /**
     * Return a LocalDateTime
     * 
     * @param year
     * @param month
     * @param day
     * @param time
     * @return
     */
    public LocalDateTime createLocalDateTime(int year, int month, int day, String time) {
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
    public boolean validFlight(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime departureDateTime,
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
    public FlightResult createFlightResult(String from, String to, LocalDateTime departureDateTime,
            LocalDateTime arrivalDateTime) {
        FlightResult flightResult = new FlightResult();
        flightResult.setStops(0);
        List<Leg> legs = new ArrayList<Leg>();
        legs.add(new Leg(from, to, departureDateTime.toString(), arrivalDateTime.toString()));
        flightResult.setLegs(legs);
        return flightResult;
    }
    
    /**
     * Create a FlightResult from a pair of Leg
     * @param departureToStop
     * @param stopToArrival
     * @return
     */
    public FlightResult createFlightResult(Leg departureToStop, Leg stopToArrival) {
        FlightResult flightResult = new FlightResult();
        flightResult.setStops(1);
        List<Leg> legs = new ArrayList<Leg>();
        legs.add(departureToStop);
        legs.add(stopToArrival);
        flightResult.setLegs(legs);
        return flightResult;
    }

}
