package com.lightingsystem.lightingsystem.Controller;

import com.lightingsystem.lightingsystem.Servics.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/led")
public class LedController {

    @Autowired
    private MqttService mqttService;

    @PostMapping("/led1")
    public String controlLed1(@RequestParam String state) {
        mqttService.publishLed1(state);  // state: "on", "off", or "auto"
        return "LED1 set to: " + state;
    }

    @PostMapping("/led2")
    public String controlLed2(@RequestParam String state) {
        mqttService.publishLed2(state);
        return "LED2 set to: " + state;
    }
}
