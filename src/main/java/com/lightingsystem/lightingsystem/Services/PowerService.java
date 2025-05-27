package com.lightingsystem.lightingsystem.Services;

import com.lightingsystem.lightingsystem.Model.SensorReading;
import com.lightingsystem.lightingsystem.Repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PowerService {

    @Autowired
    private SensorReadingRepository repository;

    public void handlePowerReading(String location, double powerWatts) {
        SensorReading reading = new SensorReading();
        reading.setLocation(location);
        reading.setPowerconsumed(powerWatts); // ➕ Add this field to your entity if not already present

        repository.save(reading);
    }
}
