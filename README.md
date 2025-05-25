🚨 ESP32 Motion-Activated Lighting System with MQTT
This project uses an ESP32 microcontroller to control two LEDs based on input from two PIR motion sensors. It supports automatic (motion-based) and manual override via MQTT messages. A backend server and a frontend dashboard allow users to monitor and control the system.

🛠 Hardware Components
1 x ESP32 Dev Board

2 x PIR Motion Sensors

2 x LEDs

2 x Resistors (220Ω for LEDs)

Jumper Wires

Breadboard

🔌 Pin Configuration
Component	ESP32 Pin
LED 1	GPIO 5
PIR Sensor 1	GPIO 13
LED 2	GPIO 4
PIR Sensor 2	GPIO 12

🌐 WiFi and MQTT Configuration
Setting	Value
WiFi SSID	"your_SSID"
WiFi Password	"your_PASSWORD"
MQTT Broker	broker.hivemq.com
Port	1883


📡 MQTT Topics
Topic	Description
home/led/led1	Control LED 1 (on, off, auto)
home/led/led2	Control LED 2 (on, off, auto)
home/sensor/pir1	PIR Sensor 1 output (1 or 0)
home/sensor/pir2	PIR Sensor 2 output (1 or 0)
home/status	System status messages

🧠 Functionality
Automatic Mode: LEDs turn on when motion is detected.

Manual Override: Control LEDs using MQTT commands.

Real-Time Monitoring: Sensor states are published live.

User Interface: Frontend web dashboard subscribes to sensor topics and publishes control messages.


🚀 How to Use
Connect the Hardware

Assemble ESP32, sensors, and LEDs according to the pin configuration above.

Power the ESP32 using USB.

Connect to MQTT Broker

Ensure your ESP32 connects to Wi-Fi.

MQTT messages will be sent to and from broker.hivemq.com.

Run the Backend
navigate to 
LightingSystem\src\main\java\com\lightingsystem\lightingsystem\LightingsystemApplication.java

This script can publish/subscribe to MQTT topics, simulate events, or interface with databases.

Launch the Frontend Dashboard

Open http://localhost:8080/homePage.html in a browser.

Use the dashboard to:

Monitor motion sensor status in real-time.

Control LEDs manually (on, off, auto).

Subscribe to MQTT Topics

Use an MQTT client like MQTT Explorer or Node-RED to:

Monitor motion events.

Send commands to control LED behavior.

