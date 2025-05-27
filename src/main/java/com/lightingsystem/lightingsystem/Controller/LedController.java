package com.lightingsystem.lightingsystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.lightingsystem.lightingsystem.Events.MotionDetectedEvent;
import com.lightingsystem.lightingsystem.Services.MqttService;

@RestController
@RequestMapping("/api/led")
public class LedController {

    @Autowired
    private MqttService mqttService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> led1AutoResetTask;
    private ScheduledFuture<?> led2AutoResetTask;

    private final long AUTO_MODE_DELAY = 10;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @PostMapping("/led1")
    public String controlLed1(@RequestParam String state) {
        mqttService.publishLed1(state);

        if (led1AutoResetTask != null && !led1AutoResetTask.isDone()) {
            led1AutoResetTask.cancel(false);
        }

        if ("on".equalsIgnoreCase(state) || "off".equalsIgnoreCase(state)) {
            led1AutoResetTask = scheduler.schedule(() -> {
                mqttService.publishLed1("auto");
                System.out.println("LED1 auto mode resumed after manual override.");
            }, AUTO_MODE_DELAY, TimeUnit.SECONDS);
        }

        return "LED1 set to: " + state;
    }

    @PostMapping("/led2")
    public String controlLed2(@RequestParam String state) {
        mqttService.publishLed2(state);

        if (led2AutoResetTask != null && !led2AutoResetTask.isDone()) {
            led2AutoResetTask.cancel(false);
        }

        if ("on".equalsIgnoreCase(state) || "off".equalsIgnoreCase(state)) {
            led2AutoResetTask = scheduler.schedule(() -> {
                mqttService.publishLed2("auto");
                System.out.println("LED2 auto mode resumed after manual override.");
            }, AUTO_MODE_DELAY, TimeUnit.SECONDS);
        }

        return "LED2 set to: " + state;
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // No timeout
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("motion").data("Test event: SSE connected!"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void sendMotionNotification(String message) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("motion").data(message));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    @EventListener
    public void onMotionDetected(MotionDetectedEvent event) {
        String location = event.getLocation();
        String turnedOffBy = event.getTurnedOffBy();

        String message;
        if (turnedOffBy == null) {
            // Motion detected, light turned ON
            message = "Motion detected in " + location + "! Light turned on.";
        } else {
            // Light turned OFF, specify who turned it off
            message = "Light turned off in " + location + " by " + turnedOffBy + ".";
        }

        System.out.println("MotionDetectedEvent received in controller: " + message);
        sendMotionNotification(message);
    }
}
