package com.lightingsystem.lightingsystem.Controller;

import com.lightingsystem.lightingsystem.Services.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    @Autowired
    private SensorReadingService sensorReadingService;

    private static final Set<String> validLocations = Set.of("garage", "reception");

    // Helper method to validate location (case-insensitive)
    private void validateLocation(String location) {
        if (location == null || !validLocations.contains(location.toLowerCase())) {
            throw new IllegalArgumentException("Invalid location: " + location + ". Valid locations: " + validLocations);
        }
    }

    @PostMapping("/motion/on")
    public void motionDetected(@RequestParam String location) {
        validateLocation(location);
        sensorReadingService.handleMotionDetected(location.toLowerCase());
    }

    @PostMapping("/motion/off")
    public void lightTurnedOff(@RequestParam String location, @RequestParam String by) {
        validateLocation(location);
        sensorReadingService.handleLightTurnedOff(location.toLowerCase(), by);
    }
}
