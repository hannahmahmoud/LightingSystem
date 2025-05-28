package com.lightingsystem.lightingsystem.Services;

import com.lightingsystem.lightingsystem.Model.SensorReading;
import com.lightingsystem.lightingsystem.Repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PowerService {

    @Autowired
    private SensorReadingRepository repository;

    public void handlePowerReading(String location, double powerWatts) {
        // Find the latest sensor reading for this location
        Optional<SensorReading> latestReadingOpt = repository.findTopByLocationAndLightTurnedOffAtIsNullOrderByLightTurnedOnAtDesc(location);

        if (latestReadingOpt.isPresent()) {
            SensorReading latestReading = latestReadingOpt.get();
            latestReading.setPowerconsumed(powerWatts);
            repository.save(latestReading);
            System.out.println("Updated powerConsumed for " + location + ": " + powerWatts + "W");
        } else {
            System.out.println("⚠️ No sensor reading found to update for location: " + location);
        }
    }
}
