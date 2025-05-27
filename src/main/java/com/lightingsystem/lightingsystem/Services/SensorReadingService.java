package com.lightingsystem.lightingsystem.Services;

import com.lightingsystem.lightingsystem.Model.SensorReading;
import com.lightingsystem.lightingsystem.Repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SensorReadingService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    public void handleMotionDetected(String location) {
        LocalDateTime now = LocalDateTime.now();

        SensorReading newReading = new SensorReading(
                location,
                true,
                now,       // motionDetectedAt
                now,       // lightTurnedOnAt
                null,      // lightTurnedOffAt - null because light is ON now
                null       // turnedOffBy - null because light not turned off yet
        );
        sensorReadingRepository.save(newReading);
    }


    public void handleLightTurnedOff(String location, String turnedOffBy) {
        Optional<SensorReading> optionalReading = sensorReadingRepository
                .findTopByLocationAndLightTurnedOffAtIsNullOrderByLightTurnedOnAtDesc(location);

        if (optionalReading.isPresent()) {
            SensorReading reading = optionalReading.get();
            reading.setLightTurnedOffAt(LocalDateTime.now());
            reading.setTurnedOffBy(turnedOffBy);
            sensorReadingRepository.save(reading);
        } else {
            System.out.println("No active light record found to turn off for location: " + location);
        }
    }
}
