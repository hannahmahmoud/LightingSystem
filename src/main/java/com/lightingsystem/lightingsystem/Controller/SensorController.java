package com.lightingsystem.lightingsystem.Controller;

import com.lightingsystem.lightingsystem.Events.MotionDetectedEvent;
import com.lightingsystem.lightingsystem.Services.MqttService;
import com.lightingsystem.lightingsystem.Services.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.event.EventListener;

import java.util.Set;
import java.util.HashMap;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    @Autowired
    private SensorReadingService sensorReadingService;

    @Autowired
    private MqttService mqttService; // Inject it

    private static final Set<String> validLocations = Set.of("garage", "reception");

    // Helper method to validate location (case-insensitive)
    private void validateLocation(String location) {
        if (location == null || !validLocations.contains(location.toLowerCase())) {
            throw new IllegalArgumentException("Invalid location: " + location + ". Valid locations: " + validLocations);
        }
    }

    // Motion detected (sensor triggered) - light turned ON
    @PostMapping("/motion/on/{location}")
    public ResponseEntity<Object> motionDetected(@PathVariable String location) {
        validateLocation(location);
        return sensorReadingService.handleMotionDetected(location.toLowerCase());
    }

    // Light turned OFF
  @PostMapping("/motion/off")
    public ResponseEntity<Object> lightTurnedOff(
        @RequestParam String location,
        @RequestParam String by,
        @RequestParam(required = false) Double powerReading
    ) {
        validateLocation(location);

        double actualPowerReading = (powerReading != null)
            ? powerReading
            : mqttService.getLatestPowerReading(location.toLowerCase());

        return sensorReadingService.handleLightTurnedOff(location.toLowerCase(), by, actualPowerReading);
    }


 
        
    

    // New endpoint: Light turned ON via website (not by sensor motion)
    @PostMapping("/light/on")

    public ResponseEntity<Object> lightTurnedOnViaWebsite(@RequestParam String location, @RequestParam(required = false) String turnedOnBy) {
        validateLocation(location);
        // If turnedOnBy is null, default to "website"
        String user = turnedOnBy == null ? "website" : turnedOnBy;
        return sensorReadingService.handleTurnOnDetectionWebsite(location.toLowerCase());
    }

    // Listen to MotionDetectedEvent and store it using SensorReadingService
    @EventListener
    public void onMotionDetected(MotionDetectedEvent event) {
        String location = event.getLocation();
        String turnedOffBy = event.getTurnedOffBy();

        System.out.println("MotionDetectedEvent received in SensorController for DB: " + location + ", by: " + turnedOffBy);

        if (turnedOffBy == null) {
            // Store as motion detected event
            sensorReadingService.handleMotionDetected(location);
        } else {
            // Store as light turned off event
            sensorReadingService.handleLightTurnedOff(
                    location.toLowerCase(),
                    turnedOffBy,
                    event.getPowerReading()
            );

        }
    }
}
