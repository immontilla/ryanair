package eu.immontilla.ryanair.client.model;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Represent a list of avalaible flights on a day - Schedules API
 * @author immontilla
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DayFlight implements Serializable {
    private static final long serialVersionUID = 1L;

    private int day;
    private Collection<Flight> flights;

    @JsonCreator
    private DayFlight(@JsonProperty("day") int day, @JsonProperty("flights") Collection<Flight> flights) {
        super();
        this.day = day;
        this.flights = flights;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Collection<Flight> getFlights() {
        return flights;
    }

    public void setFlights(Collection<Flight> flights) {
        this.flights = flights;
    }

}
