package edu.uwb.gymapp.models;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Proxy(lazy = false)
public class Session {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Workout workout;

    @ManyToOne
    private Trainer trainer;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day")
    private DayOfWeek dayOfWeek;

    private Integer capacity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}


