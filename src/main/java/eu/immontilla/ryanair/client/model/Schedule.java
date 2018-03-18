package eu.immontilla.ryanair.client.model;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a schedule - Schedules API
 * 
 * @author immontilla
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private int month;
    private Collection<DayFlight> days;

    @JsonCreator
    public Schedule(@JsonProperty("month") int month, @JsonProperty("days") Collection<DayFlight> days) {
        super();
        this.month = month;
        this.days = days;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Collection<DayFlight> getDays() {
        return days;
    }

    public void setDays(Collection<DayFlight> days) {
        this.days = days;
    }

}
