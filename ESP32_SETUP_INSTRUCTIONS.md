# ESP32 Setup for AgriFarm Mobile App

## Quick Setup

### 1. Upload ESP32 Code
- Open `ESP32_WITH_HTTP_SERVER.ino` in Arduino IDE
- Update WiFi credentials if needed
- Upload to ESP32
- Open Serial Monitor to see IP address

### 2. Update Android App
- Open `SensorRepository.kt`
- Find line: `private val esp32Ip = "192.168.29.247"`
- Replace with your ESP32 IP from Serial Monitor
- Rebuild app

### 3. Test Connection
- Ensure phone and ESP32 are on same WiFi
- Open browser on phone
- Go to `http://YOUR_ESP32_IP/sensor`
- You should see JSON sensor data

## Troubleshooting

**Device shows offline:**
- Check ESP32 Serial Monitor for IP
- Update IP in SensorRepository.kt
- Ensure same WiFi network
- Try pinging ESP32 IP from phone

**No sensor data:**
- Check sensor connections
- Open Serial Monitor to see readings
- Verify sensors are working

**Connection timeout:**
- Reduce delay in SensorRepository (currently 3000ms)
- Check firewall settings
- Restart ESP32

## Current ESP32 IP
Check Serial Monitor after uploading code to see actual IP address.
