package eu.immontilla.ryanair.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Part of flight result.
 * 
 * @author immontilla
 */
@ApiModel
public class Leg {
    private String departureAirport;
    private String arrivalAirport;
    private String departureDateTime;
    private String arrivalDateTime;

    public Leg() {
        super();
    }

    public Leg(String departureAirport, String arrivalAirport, String departureDateTime, String arrivalDateTime) {
        super();
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    @ApiModelProperty(position = 1, required = true, value = "A departure airport IATA code")
    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    @ApiModelProperty(position = 2, required = true, value = "An arrival airport IATA code")
    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    @ApiModelProperty(position = 3, required = true, value = "A departure datetime in the departure airport timezone in ISO format")
    public String getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(String departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    @ApiModelProperty(position = 4, required = true, value = "An arrival datetime in the arrival airport timezone in ISO format")
    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(String arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Leg [");
        if (departureAirport != null)
            builder.append("departureAirport=").append(departureAirport).append(", ");
        if (arrivalAirport != null)
            builder.append("arrivalAirport=").append(arrivalAirport).append(", ");
        if (departureDateTime != null)
            builder.append("departureDateTime=").append(departureDateTime).append(", ");
        if (arrivalDateTime != null)
            builder.append("arrivalDateTime=").append(arrivalDateTime);
        builder.append("]");
        return builder.toString();
    }

}
