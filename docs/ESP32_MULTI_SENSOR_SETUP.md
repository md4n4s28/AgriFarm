# ESP32 Multi-Sensor Setup Guide

## Hardware Architecture for Multiple Sensors

### Overview
This guide explains how to connect multiple sensors of the same type to ESP32 and detect individual sensor failures.

---

## 🔌 Hardware Connection Methods

### Method 1: I2C Multiplexer (Recommended for DHT/I2C Sensors)
**Use TCA9548A I2C Multiplexer**

```
ESP32          TCA9548A         Sensors
GPIO21 (SDA) → SDA
GPIO22 (SCL) → SCL
3.3V         → VCC
GND          → GND
             → SD0/SC0 → DHT Sensor #1
             → SD1/SC1 → DHT Sensor #2
             → SD2/SC2 → DHT Sensor #3
             → SD3/SC3 → DHT Sensor #4
```

### Method 2: Analog Multiplexer (For Analog Sensors)
**Use CD74HC4067 16-Channel Multiplexer**

```
ESP32          CD74HC4067       Sensors
GPIO34 (ADC) → SIG
GPIO25       → S0 (Select bit 0)
GPIO26       → S1 (Select bit 1)
GPIO27       → S2 (Select bit 2)
GPIO14       → S3 (Select bit 3)
3.3V         → VCC
GND          → GND
             → C0  → Soil Sensor #1
             → C1  → Soil Sensor #2
             → C2  → Soil Sensor #3
             → C3  → Gas Sensor #1
             → C4  → Gas Sensor #2
```

### Method 3: Direct GPIO (For Digital Sensors)
**Connect each sensor to separate GPIO pins**

```
ESP32 GPIO    Sensor
GPIO32      → DHT Sensor #1
GPIO33      → DHT Sensor #2
GPIO25      → DHT Sensor #3
GPIO26      → Rain Sensor #1
GPIO27      → Rain Sensor #2
```

---

## 📡 ESP32 Arduino Code

### Complete Multi-Sensor Implementation

```cpp
#include <WiFi.h>
#include <WebServer.h>
#include <DHT.h>
#include <Wire.h>

// WiFi credentials
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

WebServer server(80);

// DHT Sensors Configuration
#define DHT_COUNT 3
#define DHT_TYPE DHT11
const int dhtPins[DHT_COUNT] = {32, 33, 25};
DHT dhtSensors[DHT_COUNT] = {
  DHT(dhtPins[0], DHT_TYPE),
  DHT(dhtPins[1], DHT_TYPE),
  DHT(dhtPins[2], DHT_TYPE)
};

// Soil Moisture Sensors (Analog Multiplexer)
#define SOIL_COUNT 3
#define MUX_SIG 34
#define MUX_S0 25
#define MUX_S1 26
#define MUX_S2 27
const int soilChannels[SOIL_COUNT] = {0, 1, 2};

// Gas Sensors (Analog Multiplexer)
#define GAS_COUNT 2
const int gasChannels[GAS_COUNT] = {3, 4};

// Rain Sensors
#define RAIN_COUNT 2
const int rainPins[RAIN_COUNT] = {26, 27};

// Sensor health tracking
bool dhtOnline[DHT_COUNT];
bool soilOnline[SOIL_COUNT];
bool gasOnline[GAS_COUNT];
bool rainOnline[RAIN_COUNT];

// Multiplexer channel selection
void selectMuxChannel(int channel) {
  digitalWrite(MUX_S0, bitRead(channel, 0));
  digitalWrite(MUX_S1, bitRead(channel, 1));
  digitalWrite(MUX_S2, bitRead(channel, 2));
  delay(10); // Settling time
}

// Read analog value from multiplexer
int readMuxChannel(int channel) {
  selectMuxChannel(channel);
  return analogRead(MUX_SIG);
}

// Check if DHT sensor is working
bool checkDHTSensor(int index) {
  float temp = dhtSensors[index].readTemperature();
  float hum = dhtSensors[index].readHumidity();
  return (!isnan(temp) && !isnan(hum));
}

// Check if analog sensor is working (not returning max/min values)
bool checkAnalogSensor(int value) {
  return (value > 10 && value < 4085); // Valid range
}

void setup() {
  Serial.begin(115200);
  
  // Initialize WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi Connected!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  
  // Initialize DHT sensors
  for (int i = 0; i < DHT_COUNT; i++) {
    dhtSensors[i].begin();
  }
  
  // Initialize multiplexer pins
  pinMode(MUX_S0, OUTPUT);
  pinMode(MUX_S1, OUTPUT);
  pinMode(MUX_S2, OUTPUT);
  pinMode(MUX_SIG, INPUT);
  
  // Initialize rain sensor pins
  for (int i = 0; i < RAIN_COUNT; i++) {
    pinMode(rainPins[i], INPUT);
  }
  
  // Setup web server
  server.on("/sensor", handleSensorRequest);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
}

void handleSensorRequest() {
  // Read all DHT sensors
  float dhtTemps[DHT_COUNT];
  float dhtHums[DHT_COUNT];
  for (int i = 0; i < DHT_COUNT; i++) {
    dhtTemps[i] = dhtSensors[i].readTemperature();
    dhtHums[i] = dhtSensors[i].readHumidity();
    dhtOnline[i] = checkDHTSensor(i);
  }
  
  // Read all soil sensors
  int soilValues[SOIL_COUNT];
  for (int i = 0; i < SOIL_COUNT; i++) {
    soilValues[i] = readMuxChannel(soilChannels[i]);
    soilOnline[i] = checkAnalogSensor(soilValues[i]);
  }
  
  // Read all gas sensors
  int gasValues[GAS_COUNT];
  for (int i = 0; i < GAS_COUNT; i++) {
    gasValues[i] = readMuxChannel(gasChannels[i]);
    gasOnline[i] = checkAnalogSensor(gasValues[i]);
  }
  
  // Read all rain sensors
  bool rainDetected[RAIN_COUNT];
  for (int i = 0; i < RAIN_COUNT; i++) {
    rainDetected[i] = digitalRead(rainPins[i]) == LOW;
    rainOnline[i] = true; // Digital sensors are always online if connected
  }
  
  // Calculate averages (only from online sensors)
  float avgTemp = 0, avgHum = 0;
  int onlineDHT = 0;
  for (int i = 0; i < DHT_COUNT; i++) {
    if (dhtOnline[i]) {
      avgTemp += dhtTemps[i];
      avgHum += dhtHums[i];
      onlineDHT++;
    }
  }
  if (onlineDHT > 0) {
    avgTemp /= onlineDHT;
    avgHum /= onlineDHT;
  }
  
  int avgSoil = 0, onlineSoil = 0;
  for (int i = 0; i < SOIL_COUNT; i++) {
    if (soilOnline[i]) {
      avgSoil += soilValues[i];
      onlineSoil++;
    }
  }
  if (onlineSoil > 0) avgSoil /= onlineSoil;
  
  int avgGas = 0, onlineGas = 0;
  for (int i = 0; i < GAS_COUNT; i++) {
    if (gasOnline[i]) {
      avgGas += gasValues[i];
      onlineGas++;
    }
  }
  if (onlineGas > 0) avgGas /= onlineGas;
  
  // Build JSON response
  String json = "{";
  
  // Legacy single values (averages)
  json += "\"temperature\":" + String(avgTemp) + ",";
  json += "\"humidity\":" + String(avgHum) + ",";
  json += "\"soil_moisture\":" + String(avgSoil) + ",";
  json += "\"soil_moisture_pct\":" + String(map(avgSoil, 4095, 0, 0, 100)) + ",";
  json += "\"gas_level\":" + String(avgGas) + ",";
  json += "\"rain_detected\":" + String(rainDetected[0] ? "true" : "false") + ",";
  json += "\"dht_ok\":" + String(onlineDHT > 0 ? "true" : "false") + ",";
  json += "\"soil_ok\":" + String(onlineSoil > 0 ? "true" : "false") + ",";
  json += "\"gas_ok\":" + String(onlineGas > 0 ? "true" : "false") + ",";
  
  // Multi-sensor arrays
  json += "\"dht_sensors\":[";
  for (int i = 0; i < DHT_COUNT; i++) {
    json += "{\"id\":" + String(i + 1) + ",";
    json += "\"temperature\":" + String(dhtTemps[i]) + ",";
    json += "\"humidity\":" + String(dhtHums[i]) + ",";
    json += "\"online\":" + String(dhtOnline[i] ? "true" : "false") + "}";
    if (i < DHT_COUNT - 1) json += ",";
  }
  json += "],";
  
  json += "\"soil_sensors\":[";
  for (int i = 0; i < SOIL_COUNT; i++) {
    json += "{\"id\":" + String(i + 1) + ",";
    json += "\"moisture\":" + String(soilValues[i]) + ",";
    json += "\"moisture_pct\":" + String(map(soilValues[i], 4095, 0, 0, 100)) + ",";
    json += "\"online\":" + String(soilOnline[i] ? "true" : "false") + "}";
    if (i < SOIL_COUNT - 1) json += ",";
  }
  json += "],";
  
  json += "\"gas_sensors\":[";
  for (int i = 0; i < GAS_COUNT; i++) {
    json += "{\"id\":" + String(i + 1) + ",";
    json += "\"level\":" + String(gasValues[i]) + ",";
    json += "\"online\":" + String(gasOnline[i] ? "true" : "false") + "}";
    if (i < GAS_COUNT - 1) json += ",";
  }
  json += "],";
  
  json += "\"rain_sensors\":[";
  for (int i = 0; i < RAIN_COUNT; i++) {
    json += "{\"id\":" + String(i + 1) + ",";
    json += "\"detected\":" + String(rainDetected[i] ? "true" : "false") + ",";
    json += "\"online\":" + String(rainOnline[i] ? "true" : "false") + "}";
    if (i < RAIN_COUNT - 1) json += ",";
  }
  json += "]";
  
  json += "}";
  
  server.send(200, "application/json", json);
  Serial.println("Sensor data sent");
}
```

---

## 🔍 Sensor Failure Detection Methods

### 1. **DHT Sensors (Temperature/Humidity)**
- **Detection**: Check if `readTemperature()` or `readHumidity()` returns `NaN`
- **Cause**: Disconnected wire, damaged sensor, or communication error
- **Code**: `!isnan(temp) && !isnan(hum)`

### 2. **Analog Sensors (Soil, Gas)**
- **Detection**: Check if reading is at extreme values (0 or 4095)
- **Cause**: Disconnected sensor returns max value, short circuit returns 0
- **Code**: `value > 10 && value < 4085`

### 3. **Digital Sensors (Rain)**
- **Detection**: Check if pin state changes over time
- **Cause**: Stuck HIGH/LOW indicates failure
- **Advanced**: Monitor if sensor never changes state for extended period

### 4. **I2C Sensors**
- **Detection**: Check I2C acknowledgment
- **Code**: `Wire.endTransmission() == 0` (0 = success)

---

## 📊 JSON Response Format

```json
{
  "temperature": 28.5,
  "humidity": 65.0,
  "soil_moisture": 2500,
  "soil_moisture_pct": 45,
  "gas_level": 2800,
  "dht_ok": true,
  "soil_ok": true,
  "gas_ok": true,
  
  "dht_sensors": [
    {"id": 1, "temperature": 28.2, "humidity": 64.0, "online": true},
    {"id": 2, "temperature": 28.8, "humidity": 66.0, "online": true},
    {"id": 3, "temperature": 0.0, "humidity": 0.0, "online": false}
  ],
  
  "soil_sensors": [
    {"id": 1, "moisture": 2450, "moisture_pct": 44, "online": true},
    {"id": 2, "moisture": 2550, "moisture_pct": 46, "online": true},
    {"id": 3, "moisture": 4095, "moisture_pct": 0, "online": false}
  ],
  
  "gas_sensors": [
    {"id": 1, "level": 2750, "online": true},
    {"id": 2, "level": 2850, "online": true}
  ],
  
  "rain_sensors": [
    {"id": 1, "detected": false, "online": true},
    {"id": 2, "detected": false, "online": true}
  ]
}
```

---

## 🛠️ Hardware Shopping List

### For 3 DHT + 3 Soil + 2 Gas + 2 Rain Setup:

| Component | Quantity | Purpose |
|-----------|----------|---------|
| ESP32 DevKit | 1 | Main controller |
| DHT11/DHT22 | 3 | Temperature/Humidity |
| Soil Moisture Sensor | 3 | Soil monitoring |
| MQ-135 Gas Sensor | 2 | Air quality |
| Rain Sensor Module | 2 | Rain detection |
| CD74HC4067 Multiplexer | 1 | Analog sensor switching |
| Jumper Wires | 50+ | Connections |
| Breadboard | 2 | Prototyping |
| 5V Power Supply | 1 | Power (2A minimum) |

---

## ⚡ Power Considerations

- **ESP32**: ~500mA peak
- **DHT11**: ~2.5mA each (7.5mA total)
- **Soil Sensors**: ~35mA each (105mA total)
- **Gas Sensors**: ~150mA each (300mA total)
- **Total**: ~1A minimum, use 2A power supply

---

## 🐛 Troubleshooting

### Sensor Shows Offline
1. Check physical connections
2. Verify power supply voltage (3.3V or 5V)
3. Test sensor individually
4. Check multiplexer channel selection

### Incorrect Readings
1. Calibrate sensors
2. Check for electromagnetic interference
3. Add pull-up resistors for I2C (4.7kΩ)
4. Increase settling time in multiplexer

### WiFi Connection Issues
1. Check SSID and password
2. Ensure 2.4GHz WiFi (ESP32 doesn't support 5GHz)
3. Move closer to router
4. Check router firewall settings

---

## 📱 App Integration

The Android app automatically:
- Detects number of sensors from JSON
- Calculates averages from online sensors only
- Shows failed sensor IDs in red warning
- Displays LED indicators for each sensor
- Updates configuration based on actual hardware

---

**Built for AgriFarm IoT System**
