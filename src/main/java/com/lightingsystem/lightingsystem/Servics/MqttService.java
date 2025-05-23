package com.lightingsystem.lightingsystem.Servics;

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
    System.out.println("Message arrived: [" + topic + "] " + message.toString());

    if (message.toString().equals("1")) {
        String location = "";
        if (topic.equals(topicPir1)) {
            location = "Reception";
        } else if (topic.equals(topicPir2)) {
            location = "Garage";
        }

        String motionMessage = "Motion detected in " + location + "! Light turned on.";
        System.out.println(motionMessage);
        eventPublisher.publishEvent(new MotionDetectedEvent(this, motionMessage));
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
