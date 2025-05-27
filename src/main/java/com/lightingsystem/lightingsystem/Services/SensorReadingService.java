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


    public ResponseEntity<Object> handleLightTurnedOff(String location, String turnedOffBy) {
        Map<String, Object>response = new HashMap<>();
        Optional<SensorReading> optionalReading = sensorReadingRepository
                .findTopByLocationAndLightTurnedOffAtIsNullOrderByLightTurnedOnAtDesc(location);

        if (optionalReading.isPresent()) {
            SensorReading reading = optionalReading.get();
            reading.setLightTurnedOffAt(LocalDateTime.now());
            reading.setTurnedOffBy(turnedOffBy);
            sensorReadingRepository.save(reading);
             response.put("status","Success");
            response.put ("Reading",reading);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
            
        } else {
             System.out.println("No active light record found to turn off for location: " + location);
            response.put("status","Failed");
            response.put ("message","No active light record found to turn off for location:");
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
        null,        // turnedOffBy - not applicable yet
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
