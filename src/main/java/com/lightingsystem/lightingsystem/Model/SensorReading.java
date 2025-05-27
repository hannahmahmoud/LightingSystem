package com.lightingsystem.lightingsystem.Model;

import java.time.LocalDateTime;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Setter
@Getter
@Entity // telling spring boot this file is going to generate a table
@Table(name = "SensorReadings")
public class SensorReading
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private boolean motionDetected;

    @Column(nullable = false)
    private LocalDateTime motionDetectedAt;

    @Column(nullable = false)
    private LocalDateTime lightTurnedOnAt;

    @Column(nullable = true)
    private LocalDateTime lightTurnedOffAt;

    @Column(length = 50, nullable = true)
    private String turnedOffBy; // "sensor" or "user"

    public SensorReading() {}

    public SensorReading(String location, boolean motionDetected, LocalDateTime motionDetectedAt, LocalDateTime lightTurnedOnAt, LocalDateTime lightTurnedOffAt, String turnedOffBy ) {
        this.location = location;
        this.motionDetected = motionDetected;
        this.motionDetectedAt = motionDetectedAt;
        this.lightTurnedOnAt = lightTurnedOnAt;
        this.lightTurnedOffAt = lightTurnedOffAt;
        this.turnedOffBy = turnedOffBy;
    }

}