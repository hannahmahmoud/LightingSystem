package com.lightingsystem.lightingsystem.Services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.lightingsystem.lightingsystem.Events.MotionDetectedEvent;

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

    private MqttClient mqttClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
                        location = "Reception";
                    } else if (topic.equals(topicPir2)) {
                        location = "Garage";
                    }

                    if (location != null) {
                        if ("1".equals(payload)) {
                            // Motion detected, light turned ON
                            eventPublisher.publishEvent(new MotionDetectedEvent(this, location, null));
                            System.out.println("Motion detected in " + location + "! Light turned on.");
                        } else if ("0".equals(payload)) {
                            // Motion stopped, light turned OFF by sensor
                            eventPublisher.publishEvent(new MotionDetectedEvent(this, location, "sensor"));
                            System.out.println("Light turned off in " + location + " by sensor.");
                        }
                        // You can add more logic for other payloads or user-triggered events here
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Message delivered");
                }
            });

            // Subscribe to PIR sensor topics
            mqttClient.subscribe(topicPir1);
            mqttClient.subscribe(topicPir2);

            System.out.println("MQTT client connected and subscribed to PIR topics.");

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
