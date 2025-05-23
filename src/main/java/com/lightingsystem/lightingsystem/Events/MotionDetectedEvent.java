package com.lightingsystem.lightingsystem.Events;

import org.springframework.context.ApplicationEvent;

public class MotionDetectedEvent extends ApplicationEvent {
    private final String message;

    public MotionDetectedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
