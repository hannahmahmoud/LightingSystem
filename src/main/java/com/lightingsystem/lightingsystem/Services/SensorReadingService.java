package com.lightingsystem.lightingsystem.Services;

import com.lightingsystem.lightingsystem.Model.SensorReading;
import com.lightingsystem.lightingsystem.Repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class SensorReadingService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    public ResponseEntity<Object> handleMotionDetected(String location) {
        Map<String, Object>response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        SensorReading newReading = new SensorReading(
                location,
                true,
                now,       // motionDetectedAt
                now,       // lightTurnedOnAt
                null,      // lightTurnedOffAt - null because light is ON now
                null,       // turnedOffBy - null because light not turned off yet
                0
        );
        sensorReadingRepository.save(newReading);
          response.put("status","Success");
            response.put ("Reading",newReading);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<Object> handleTurnOnDetectionWebsite(String location) {
        Map<String, Object>response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        SensorReading newReading = new SensorReading(
                location,
                true,
                now,       // motionDetectedAt
                now,       // lightTurnedOnAt
                null,      // lightTurnedOffAt - null because light is ON now
                "website",       // turnedOffBy - null because light not turned off yet
                0
        );
        sensorReadingRepository.save(newReading);
        response.put("status","Success");
        response.put ("Reading",newReading);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




    public ResponseEntity<Object> handleLightTurnedOff(String location, String turnedOffBy, double powerReadingWatts) {
    Map<String, Object> response = new HashMap<>();
    Optional<SensorReading> optionalReading = sensorReadingRepository
            .findTopByLocationAndLightTurnedOffAtIsNullOrderByLightTurnedOnAtDesc(location);

    if (optionalReading.isPresent()) {
        SensorReading reading = optionalReading.get();
        LocalDateTime now = LocalDateTime.now();
        reading.setLightTurnedOffAt(now);
        reading.setTurnedOffBy(turnedOffBy);

        // Calculate duration in seconds
        long durationSeconds = java.time.Duration.between(reading.getLightTurnedOnAt(), now).getSeconds();
        
        // Power consumed = power rating (Watts) * duration (seconds) / 3600
        double powerConsumedWh = (powerReadingWatts * durationSeconds) / 3600.0;
        reading.setPowerconsumed(powerConsumedWh);

        sensorReadingRepository.save(reading);
        response.put("status", "Success");
        response.put("Reading", reading);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } else {
        response.put("status", "Failed");
        response.put("message", "No active light record found to turn off for location: " + location);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

    public ResponseEntity<Object> handleLightTurnedOn(String location, String turnedOnBy) {
    Map<String, Object> response = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();

    SensorReading newReading = new SensorReading(
        location,
        false,       // motionDetected = false since website triggered
        null,        // motionDetectedAt - no motion here
        now,         // lightTurnedOnAt
        null,        // lightTurnedOffAt
        null,        // turnedONBy - not applicable yet
        0
    );
    
    // If you want, add a field to record who turned it on (turnedOnBy)
    // For now, maybe use the 'turnedOffBy' field or add new field in SensorReading model
    
    sensorReadingRepository.save(newReading);
    
    response.put("status", "Success");
    response.put("Reading", newReading);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

}
