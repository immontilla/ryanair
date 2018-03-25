package eu.immontilla.ryanair.client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Represent a flight - Schedules API
 * @author immontilla
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String number;
    private String departureTime;
    private String arrivalTime;

    @JsonCreator
    public Flight(@JsonProperty("number") String number, @JsonProperty("departureTime") String departureTime,
            @JsonProperty("arrivalTime") String arrivalTime) {
        super();
        this.number = number;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Flight() {
        super();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Flight [");
        if (number != null)
            builder.append("number=").append(number).append(", ");
        if (departureTime != null)
            builder.append("departureTime=").append(departureTime).append(", ");
        if (arrivalTime != null)
            builder.append("arrivalTime=").append(arrivalTime);
        builder.append("]");
        return builder.toString();
    }

}
