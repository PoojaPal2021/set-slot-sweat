package edu.uwb.gymapp.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to encapsulate the workout history to be sent in a response message
 */
public class SessionHistory {

    private LocalDate joinDate;

    private long weeksSinceJoined;

    private long totalSessions;
    private long totalAttended;
    private List<WorkoutHistory> workoutHistory = new ArrayList<>();

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getTotalAttended() {
        return totalAttended;
    }

    public void setTotalAttended(long totalAttended) {
        this.totalAttended = totalAttended;
    }

    public List<WorkoutHistory> getWorkoutHistory() {
        return workoutHistory;
    }

    public void addWorkoutHistory(WorkoutHistory workoutHistory) {
        this.workoutHistory.add(workoutHistory);
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public long getWeeksSinceJoined() {
        return weeksSinceJoined;
    }

    public void setWeeksSinceJoined(long weeksSinceJoined) {
        this.weeksSinceJoined = weeksSinceJoined;
    }
}
