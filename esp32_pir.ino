#include <WiFi.h> 
#include <PubSubClient.h>

// --- WiFi credentials ---
const char* ssid = "Galaxy A54 5G DA99";
const char* password = "yasmin1234";

// --- MQTT Broker settings ---
const char* mqtt_server = "broker.hivemq.com"; // Public broker

WiFiClient espClient;
PubSubClient client(espClient);

// --- Pin Configuration ---
const int led1Pin = 5;
const int pir1Pin = 13; 
const int led2Pin = 4;
const int pir2Pin = 12;

// --- MQTT Topics ---
const char* topic_led1 = "home/led/led1";
const char* topic_led2 = "home/led/led2";
const char* topic_pir1 = "home/sensor/pir1";
const char* topic_pir2 = "home/sensor/pir2";
const char* topic_status = "home/status";  // Welcome message topic

// --- Control Variables ---
bool autoControlLED1 = true;
bool autoControlLED2 = true;
int manualStateLED1 = LOW;
int manualStateLED2 = LOW;
int lastPir1State = LOW;
int lastPir2State = LOW;

void setup_wifi() {
  Serial.println();
  Serial.print("Connecting to WiFi: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  // Loop until connected
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.print("WiFi connected. IP address: ");
  Serial.println(WiFi.localIP());
}

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]: ");

  String messageTemp;
  for (unsigned int i = 0; i < length; i++) {
    messageTemp += (char)payload[i];
  }
  Serial.println(messageTemp);

  // LED1 control
  if (String(topic) == topic_led1) {
    if (messageTemp.equalsIgnoreCase("on")) {
      autoControlLED1 = false;
      manualStateLED1 = HIGH;
    } else if (messageTemp.equalsIgnoreCase("off")) {
      autoControlLED1 = false;
      manualStateLED1 = LOW;
    } else if (messageTemp.equalsIgnoreCase("auto")) {
      autoControlLED1 = true;
    }
  }
  // LED2 control
  else if (String(topic) == topic_led2) {
    if (messageTemp.equalsIgnoreCase("on")) {
      autoControlLED2 = false;
      manualStateLED2 = HIGH;
    } else if (messageTemp.equalsIgnoreCase("off")) {
      autoControlLED2 = false;
      manualStateLED2 = LOW;
    } else if (messageTemp.equalsIgnoreCase("auto")) {
      autoControlLED2 = true;
    }
  }
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);

    if (client.connect(clientId.c_str())) {
      Serial.println("connected");

      client.subscribe(topic_led1);
      client.subscribe(topic_led2);
      Serial.println("Subscribed to LED control topics.");

      // --- Publish welcome message ---
      String msg = "ESP32 PIR system online, IP: " + WiFi.localIP().toString();
      client.publish(topic_status, msg.c_str());

    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" ; retrying in 5 seconds");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);
  delay(1000);  // Give serial some time to initialize
  Serial.println("Setup started");

  pinMode(led1Pin, OUTPUT);
  pinMode(led2Pin, OUTPUT);
  pinMode(pir1Pin, INPUT);
  pinMode(pir2Pin, INPUT);

  setup_wifi();

  client.setServer(mqtt_server, 1883);
  client.setCallback(mqttCallback);

  Serial.println("Setup finished");
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi disconnected! Reconnecting...");
    setup_wifi();
  }

  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  int pir1State = digitalRead(pir1Pin);
  int pir2State = digitalRead(pir2Pin);

  // 👇 This is your debug line
  Serial.print("Raw PIR1: ");
  Serial.print(pir1State);
  Serial.print(" | Raw PIR2: ");
  Serial.println(pir2State);

  // PIR1 state publish
  if (pir1State != lastPir1State) {
    lastPir1State = pir1State;
    String payload = String(pir1State);
    client.publish(topic_pir1, payload.c_str());
    Serial.print("Published PIR1 state: ");
    Serial.println(payload);
  }

  // PIR2 state publish
  if (pir2State != lastPir2State) {
    lastPir2State = pir2State;
    String payload = String(pir2State);
    client.publish(topic_pir2, payload.c_str());
    Serial.print("Published PIR2 state: ");
    Serial.println(payload);
  }

  // LED controls
  if (autoControlLED1) {
    digitalWrite(led1Pin, pir1State);
  } else {
    digitalWrite(led1Pin, manualStateLED1);
  }

  if (autoControlLED2) {
    digitalWrite(led2Pin, pir2State);
  } else {
    digitalWrite(led2Pin, manualStateLED2);
  }

  delay(500);
}


