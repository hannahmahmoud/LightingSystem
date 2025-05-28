package com.lightingsystem.lightingsystem.Services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.lightingsystem.lightingsystem.Events.MotionDetectedEvent;

import java.util.HashMap;
import java.util.Map;

@Service
public class MqttService {

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.topic.led1}")
    private String topicLed1;

    @Value("${mqtt.topic.led2}")
    private String topicLed2;

    @Value("${mqtt.topic.pir1}")
    private String topicPir1;

    @Value("${mqtt.topic.pir2}")
    private String topicPir2;

    @Value("${mqtt.topic.power1}")
    private String topicPower1;

    @Value("${mqtt.topic.power2}")
    private String topicPower2;

    private MqttClient mqttClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PowerService powerService;

    // Store the latest power readings for each location
    private final Map<String, Double> latestPowerReadings = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("MQTT Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = message.toString();
                    System.out.println("Message arrived: [" + topic + "] " + payload);

                    String location = null;
                    if (topic.equals(topicPir1)) {
                        location = "reception";
                    } else if (topic.equals(topicPir2)) {
                        location = "garage";
                    }

                    // Handle motion detection (PIR sensor)
                    if (location != null) {
                        double powerReading = latestPowerReadings.getOrDefault(location, 0.0);

                        if ("0".equals(payload)) {
                            // Motion detected, light turned ON
                            eventPublisher.publishEvent(new MotionDetectedEvent(this, location, null, powerReading));
                            System.out.println("Motion detected in " + location + "! Light turned on. Power: " + powerReading + "W");
                        } else if ("1".equals(payload)) {
                            // Motion stopped, light turned OFF by sensor
                            eventPublisher.publishEvent(new MotionDetectedEvent(this, location, "sensor", powerReading));
                            System.out.println("Light turned off in " + location + " by sensor. Power: " + powerReading + "W");
                        }
                    }

                    // Handle power readings
                    if (topic.equals(topicPower1)) {
                        double power = Double.parseDouble(payload);
                        latestPowerReadings.put("reception", power);
                        powerService.handlePowerReading("reception", power);
                        System.out.println("Power reading for reception: " + payload + " W");
                    } else if (topic.equals(topicPower2)) {
                        double power = Double.parseDouble(payload);
                        latestPowerReadings.put("garage", power);
                        powerService.handlePowerReading("garage", power);
                        System.out.println("Power reading for garage: " + payload + " W");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Message delivered");
                }
            });

            // Subscribe to relevant topics
            mqttClient.subscribe(topicPir1);
            mqttClient.subscribe(topicPir2);
            mqttClient.subscribe(topicPower1);
            mqttClient.subscribe(topicPower2);

            System.out.println("MQTT client connected and subscribed to topics.");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishLed1(String message) {
        publish(topicLed1, message);
    }

    public void publishLed2(String message) {
        publish(topicLed2, message);
    }

    private void publish(String topic, String message) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.publish(topic, new MqttMessage(message.getBytes()));
                System.out.println("Published to " + topic + ": " + message);
            } else {
                System.out.println("MQTT client not connected.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (mqttClient != null) {
                mqttClient.disconnect();
                mqttClient.close();
                System.out.println("MQTT client disconnected and closed.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
