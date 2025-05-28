package com.lightingsystem.lightingsystem.Events;

import org.springframework.context.ApplicationEvent;

public class MotionDetectedEvent extends ApplicationEvent {
    private final String location;
    private final String turnedOffBy; // "sensor", "user", or null if light is ON
    private final double powerReading; // New field to capture power reading

    public MotionDetectedEvent(Object source, String location, String turnedOffBy, double powerReading) {
        super(source);
        this.location = location;
        this.turnedOffBy = turnedOffBy;
        this.powerReading = powerReading;
    }

    public String getLocation() {
        return location;
    }

    public String getTurnedOffBy() {
        return turnedOffBy;
    }

    public double getPowerReading() {
        return powerReading;
    }

    public String getMessage() {
        if (turnedOffBy == null) {
            return "Motion detected in " + location + "! Light turned on.";
        } else {
            return "Light turned off in " + location + " by " + turnedOffBy + ". Power reading: " + powerReading + "W";
        }
    }
}
