package eu.immontilla.ryanair.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * Represent a flight result
 * @author immontilla
 *
 */
@ApiModel
public class FlightResult {
    private int stops;
    private List<Leg> legs;

    public FlightResult() {
        super();
    }

    public FlightResult(int stops, List<Leg> legs) {
        super();
        this.stops = stops;
        this.legs = legs;
    }

    @ApiModelProperty(position = 1, required = true, value = "Number of stops (in our scenario, 0 for direct flights or, 1 for non-direct flights).")
    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    @ApiModelProperty(position = 2, required = true, value = "List of legs (in our scenario 1, for direct flights or, 2 for direct flights ).")
    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

}
