package eu.immontilla.ryanair.client.model;

public class Stop {
    private Route from;
    private Route to;

    public Stop() {
        super();
    }

    public Stop(Route from, Route to) {
        super();
        this.from = from;
        this.to = to;
    }

    public Route getFrom() {
        return from;
    }

    public Route getTo() {
        return to;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Stop [");
        if (from != null)
            builder.append("from=").append(from).append(", ");
        if (to != null)
            builder.append("to=").append(to);
        builder.append("]");
        return builder.toString();
    }

}
