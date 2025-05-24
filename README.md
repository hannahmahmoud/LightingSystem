# ESP32 Motion-Activated Lighting System with MQTT

This project uses an ESP32 microcontroller to control two LEDs based on input from two PIR motion sensors. It also supports manual override of the LEDs using MQTT messages. The system publishes sensor states and responds to control messages via MQTT topics.

## 🛠 Hardware Components

- 1 x ESP32 Dev Board
- 2 x PIR Motion Sensors
- 2 x LEDs
- Resistors (220Ω for LEDs)
- Jumper Wires
- Breadboard

## 🔌 Pin Configuration

| Component    | ESP32 Pin |
|--------------|-----------|
| LED 1        | GPIO 5    |
| PIR Sensor 1 | GPIO 13   |
| LED 2        | GPIO 4    |
| PIR Sensor 2 | GPIO 12   |

## 🌐 WiFi and MQTT

- **WiFi SSID:** Galaxy A54 5G DA99  
- **WiFi Password:** yasmin1234  
- **MQTT Broker:** `broker.hivemq.com`  
- **Port:** 1883

## 📡 MQTT Topics

| Topic               | Description                        |
|---------------------|------------------------------------|
| `home/led/led1`     | Control LED 1 (`on`, `off`, `auto`)|
| `home/led/led2`     | Control LED 2 (`on`, `off`, `auto`)|
| `home/sensor/pir1`  | PIR Sensor 1 output                |
| `home/sensor/pir2`  | PIR Sensor 2 output                |
| `home/status`       | Status messages from ESP32         |

## 🧠 Functionality

- Automatically turns LEDs on when motion is detected.
- Allows manual control of LEDs via MQTT messages.
- Publishes motion sensor states to MQTT topics.

## 📂 File Structure


## ✅ How to Use

1. Open `ESP_LightingSystem.ino` in Arduino IDE.
2. Connect your ESP32 board.
3. Upload the sketch.
4. Use an MQTT client (like MQTT Explorer or Node-RED) to monitor topics and send commands.

## 🔒 Note

Avoid using personal Wi-Fi credentials and passwords in public repositories. Replace them with placeholders like `"your_SSID"` and `"your_PASSWORD"` before sharing.
