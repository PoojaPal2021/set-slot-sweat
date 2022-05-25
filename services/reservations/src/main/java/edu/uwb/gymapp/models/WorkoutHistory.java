package edu.uwb.gymapp.models;

/**
 * Encapsulates the workout history to be sent in a response message
 */
public class WorkoutHistory {
    private String name;
    private long total;
    private long attended;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAttended() {
        return attended;
    }

    public void setAttended(long attended) {
        this.attended = attended;
    }
}
