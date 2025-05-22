package com.lightingsystem.lightingsystem.Servics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
                    // Handle sensor messages here if needed
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Message delivered");
                }
            });

            mqttClient.subscribe(topicPir1);
            mqttClient.subscribe(topicPir2);

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
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}