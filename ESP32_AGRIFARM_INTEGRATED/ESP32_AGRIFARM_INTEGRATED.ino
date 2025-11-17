// -----------------------------------------------------------
// AGRIFARM IoT – INTEGRATED SENSOR SYSTEM
// Enhanced version with Supabase integration for AgriFarm App
// Compatible with SN74HC595, OLED, DHT11, Soil, Rain, Gas sensors
// -----------------------------------------------------------

#include <WiFi.h>
#include <HTTPClient.h>
#include <WebServer.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>
#include "DHT.h"
#include <ArduinoJson.h>

// ---------------- WiFi / API ----------------
const char* ssid = "Moto";
const char* password = "anas@1234";

// AgriFarm Backend - Update with your Supabase Function URL
String supabaseURL = "https://mxvpaakfigpnorepgmkw.supabase.co/functions/v1/sensor-data";
String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14dnBhYWtmaWdwbm9yZXBnbWt3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMwMDcwMzEsImV4cCI6MjA3ODU4MzAzMX0.1vOe2zALvSW73zA37eLEZvtDSajCUKOBtaWJNqwPz-w";

// Fallback webhook for testing
String webhookURL = "https://eob86s60nw99fn5.m.pipedream.net";

// Device ID (unique for each ESP32)
String deviceId = "ESP32_FARM_001";

// HTTP Server for mobile app
WebServer server(80);

// ---------------- OLED ----------------
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET -1
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// ---------------- DHT ----------------
#define DHTPIN 15
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// ---------------- Pins ----------------
#define SOIL_A 34
#define SOIL_D 27
#define RAIN_A 32
#define RAIN_D 26
#define GAS_A 35
#define BUZZER 5

// 74HC595 pins
#define DATA_PIN 23
#define LATCH_PIN 19
#define CLOCK_PIN 18

// LED bit indexes on 595
#define LED_SOIL_HEALTH 0
#define LED_DHT_HEALTH 1
#define LED_GAS_HEALTH 2
#define LED_RAIN_HEALTH 3
#define LED_WARNING 4
#define LED_CRITICAL 5
#define LED_SOIL_DRY 6
#define LED_SYSTEM_OK 7

// WiFi LED
#define LED_WIFI 13

// ---------------- Variables ----------------
unsigned long lastSend = 0;
unsigned long sendInterval = 15000; // Send every 15 seconds

float hum = NAN;
float tempC = NAN;
int soilAnalog = -1, gasVal = -1, rainAnalog = -1;
bool soilDO = false, rainDO = false;
bool soilOK = false, gasOK = false, rainOK = false, dhtOK = false;
bool wifiOK = false;
byte ledBits = 0;

// Calibration values (adjust based on your sensors)
const int SOIL_WET = 1500;
const int SOIL_DRY = 3500;
const int GAS_NORMAL = 2000;
const int GAS_WARNING = 3000;
const int GAS_CRITICAL = 3600;

// ---------------- ADC Setup ----------------
void adcSetup() {
  analogSetPinAttenuation(SOIL_A, ADC_11db);
  analogSetPinAttenuation(GAS_A, ADC_11db);
  analogSetPinAttenuation(RAIN_A, ADC_11db);
  analogSetWidth(12); // 0..4095
}

// ---------------- Helpers ----------------
int readAnalogStable(int pin, int samples = 5, int delayMs = 5) {
  int minV = 4096, maxV = -1;
  long sum = 0;
  for (int i = 0; i < samples; i++) {
    int v = analogRead(pin);
    sum += v;
    if (v < minV) minV = v;
    if (v > maxV) maxV = v;
    delay(delayMs);
  }
  int avg = sum / samples;
  if ((maxV - minV) < 3) {
    if (avg < 10 || avg > 4085) return -1;
  }
  return avg;
}

float safeReadHumidity() {
  float h = dht.readHumidity();
  if (!isnan(h) && h >= 0.0 && h <= 100.0) return h;
  delay(200);
  h = dht.readHumidity();
  if (!isnan(h) && h >= 0.0 && h <= 100.0) return h;
  return NAN;
}

float safeReadTemperature() {
  float t = dht.readTemperature();
  if (!isnan(t) && t >= -40.0 && t <= 80.0) return t;
  delay(200);
  t = dht.readTemperature();
  if (!isnan(t) && t >= -40.0 && t <= 80.0) return t;
  return NAN;
}

// ---------------- Shift Register ----------------
void shiftOutData(byte data) {
  digitalWrite(LATCH_PIN, LOW);
  shiftOut(DATA_PIN, CLOCK_PIN, MSBFIRST, data);
  digitalWrite(LATCH_PIN, HIGH);
}

// ---------------- Setup ----------------
void setup() {
  Serial.begin(115200);
  adcSetup();

  pinMode(SOIL_D, INPUT_PULLUP);
  pinMode(RAIN_D, INPUT_PULLUP);
  pinMode(BUZZER, OUTPUT);
  pinMode(LED_WIFI, OUTPUT);
  pinMode(DATA_PIN, OUTPUT);
  pinMode(LATCH_PIN, OUTPUT);
  pinMode(CLOCK_PIN, OUTPUT);
  
  shiftOutData(0x00);

  // OLED Init
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("OLED init fail");
    for (;;);
  }
  
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.println("AgriFarm IoT");
  display.println("Booting...");
  display.display();

  dht.begin();
  delay(500);

  // WiFi Connect
  WiFi.begin(ssid, password);
  display.println("Connecting WiFi...");
  display.display();
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  
  wifiOK = (WiFi.status() == WL_CONNECTED);
  digitalWrite(LED_WIFI, wifiOK ? HIGH : LOW);

  display.clearDisplay();
  display.setCursor(0, 0);
  if (wifiOK) {
    display.println("WiFi Connected!");
    display.print("IP: ");
    display.println(WiFi.localIP());
    Serial.println("\nWiFi Connected: " + WiFi.localIP().toString());
  } else {
    display.println("WiFi Failed!");
    Serial.println("\nWiFi Connection Failed");
  }
  display.display();
  delay(2000);

  // Start HTTP server for mobile app
  server.on("/sensor", handleSensorRequest);
  server.on("/", handleRoot);
  server.begin();
  Serial.println("HTTP server started on port 80");
  
  Serial.println("AgriFarm IoT System Ready");
}

// ---------------- Update Sensors ----------------
void updateSensors() {
  // DHT - Temperature & Humidity
  hum = safeReadHumidity();
  tempC = safeReadTemperature();
  dhtOK = !isnan(hum) && !isnan(tempC);

  // Analog Sensors
  int s = readAnalogStable(SOIL_A);
  soilAnalog = (s < 0) ? -1 : s;
  soilOK = (s >= 0);

  int g = readAnalogStable(GAS_A);
  gasVal = (g < 0) ? -1 : g;
  gasOK = (g >= 0);

  int r = readAnalogStable(RAIN_A);
  rainAnalog = (r < 0) ? -1 : r;
  rainOK = (r >= 0);

  // Digital Sensors
  soilDO = digitalRead(SOIL_D);
  rainDO = digitalRead(RAIN_D);
}

// ---------------- LED & Alerts ----------------
void updateLEDsAndAlerts() {
  byte bits = 0;
  bool anyDanger = false;
  bool anyWarning = false;

  // Health LEDs
  if (dhtOK) bits |= (1 << LED_DHT_HEALTH);
  if (soilOK) bits |= (1 << LED_SOIL_HEALTH);
  if (gasOK) bits |= (1 << LED_GAS_HEALTH);
  if (rainOK) bits |= (1 << LED_RAIN_HEALTH);

  // Soil Dryness
  if (soilOK && soilAnalog > SOIL_DRY) {
    bits |= (1 << LED_SOIL_DRY);
    anyWarning = true;
  }

  // Gas Levels
  if (gasOK) {
    if (gasVal > GAS_CRITICAL) {
      bits |= (1 << LED_CRITICAL);
      anyDanger = true;
    } else if (gasVal > GAS_WARNING) {
      bits |= (1 << LED_WARNING);
      anyWarning = true;
    }
  }

  // Rain Detection
  if (rainOK && rainDO == LOW) {
    bits |= (1 << LED_WARNING);
  }

  if (!anyDanger && !anyWarning) {
    bits |= (1 << LED_SYSTEM_OK);
  }

  shiftOutData(bits);
  ledBits = bits;

  // Buzzer Control
  if (anyDanger) {
    tone(BUZZER, 1200);
  } else {
    noTone(BUZZER);
    static unsigned long lastWarn = 0;
    if (anyWarning && millis() - lastWarn > 15000) {
      tone(BUZZER, 2000, 120);
      lastWarn = millis();
    }
  }
}

// ---------------- Display ----------------
void updateDisplay() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setCursor(0, 0);
  
  display.println("=== AgriFarm IoT ===");
  
  if (dhtOK) {
    display.printf("Temp: %.1fC\n", tempC);
    display.printf("Hum: %.1f%%\n", hum);
  } else {
    display.println("DHT: N/A");
  }

  if (soilOK) {
    int soilPct = map(soilAnalog, SOIL_WET, SOIL_DRY, 100, 0);
    soilPct = constrain(soilPct, 0, 100);
    display.printf("Soil: %d%% (%d)\n", soilPct, soilAnalog);
  } else {
    display.println("Soil: N/A");
  }

  if (gasOK) {
    display.printf("Gas: %d\n", gasVal);
  } else {
    display.println("Gas: N/A");
  }

  if (rainOK) {
    display.printf("Rain: %s\n", rainDO == LOW ? "YES" : "NO");
  }

  display.printf("WiFi: %s", wifiOK ? "OK" : "NO");
  display.display();
}

// ---------------- HTTP Handlers ----------------
void handleRoot() {
  server.send(200, "text/plain", "AgriFarm ESP32 - Use /sensor endpoint");
}

void handleSensorRequest() {
  server.sendHeader("Access-Control-Allow-Origin", "*");
  
  // Update LED status before sending
  updateLEDsAndAlerts();
  
  int soilPct = soilOK ? map(soilAnalog, SOIL_WET, SOIL_DRY, 100, 0) : -999;
  soilPct = constrain(soilPct, 0, 100);
  
  String json = "{";
  json += "\"temperature\":" + String(dhtOK ? tempC : -999) + ",";
  json += "\"humidity\":" + String(dhtOK ? hum : -999) + ",";
  json += "\"soil_moisture\":" + String(soilOK ? soilAnalog : -999) + ",";
  json += "\"soil_moisture_pct\":" + String(soilPct) + ",";
  json += "\"gas_level\":" + String(gasOK ? gasVal : -999) + ",";
  json += "\"rain_detected\":" + String(rainOK && rainDO == LOW ? "true" : "false") + ",";
  json += "\"rain_analog\":" + String(rainOK ? rainAnalog : -999) + ",";
  json += "\"dht_ok\":" + String(dhtOK ? "true" : "false") + ",";
  json += "\"soil_ok\":" + String(soilOK ? "true" : "false") + ",";
  json += "\"gas_ok\":" + String(gasOK ? "true" : "false") + ",";
  json += "\"rain_ok\":" + String(rainOK ? "true" : "false") + ",";
  json += "\"led_soil_health\":" + String((ledBits & (1 << LED_SOIL_HEALTH)) ? "true" : "false") + ",";
  json += "\"led_dht_health\":" + String((ledBits & (1 << LED_DHT_HEALTH)) ? "true" : "false") + ",";
  json += "\"led_gas_health\":" + String((ledBits & (1 << LED_GAS_HEALTH)) ? "true" : "false") + ",";
  json += "\"led_rain_health\":" + String((ledBits & (1 << LED_RAIN_HEALTH)) ? "true" : "false") + ",";
  json += "\"led_warning\":" + String((ledBits & (1 << LED_WARNING)) ? "true" : "false") + ",";
  json += "\"led_critical\":" + String((ledBits & (1 << LED_CRITICAL)) ? "true" : "false") + ",";
  json += "\"led_soil_dry\":" + String((ledBits & (1 << LED_SOIL_DRY)) ? "true" : "false") + ",";
  json += "\"led_system_ok\":" + String((ledBits & (1 << LED_SYSTEM_OK)) ? "true" : "false");
  json += "}";
  
  server.send(200, "application/json", json);
  Serial.println("Mobile app request served");
}

// ---------------- Send to Server ----------------
void sendToServer() {
  if (!wifiOK) {
    Serial.println("No WiFi - skipping send");
    return;
  }

  HTTPClient http;
  
  // Create JSON payload
  StaticJsonDocument<512> doc;
  doc["device_id"] = deviceId;
  doc["timestamp"] = millis();
  
  // Sensor data
  doc["temperature"] = dhtOK ? tempC : -999;
  doc["humidity"] = dhtOK ? hum : -999;
  doc["soil_moisture"] = soilOK ? soilAnalog : -999;
  doc["soil_moisture_pct"] = soilOK ? map(soilAnalog, SOIL_WET, SOIL_DRY, 100, 0) : -999;
  doc["gas_level"] = gasOK ? gasVal : -999;
  doc["rain_detected"] = rainOK ? (rainDO == LOW) : false;
  doc["rain_analog"] = rainOK ? rainAnalog : -999;
  
  // Status flags
  doc["dht_ok"] = dhtOK;
  doc["soil_ok"] = soilOK;
  doc["gas_ok"] = gasOK;
  doc["rain_ok"] = rainOK;
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  Serial.println("Sending: " + jsonString);

  // Try Supabase first
  http.begin(supabaseURL);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("apikey", supabaseKey);
  http.addHeader("Authorization", "Bearer " + supabaseKey);
  
  int code = http.POST(jsonString);
  
  if (code > 0) {
    Serial.printf("Supabase Response: %d\n", code);
    String response = http.getString();
    Serial.println(response);
  } else {
    Serial.printf("Supabase Error: %d\n", code);
    
    // Fallback to webhook
    http.end();
    http.begin(webhookURL);
    http.addHeader("Content-Type", "application/json");
    code = http.POST(jsonString);
    Serial.printf("Webhook Response: %d\n", code);
  }
  
  http.end();
}

// ---------------- Loop ----------------
unsigned long lastSensorRead = 0;
unsigned long sensorInterval = 2000;
unsigned long lastDisplayUpdate = 0;
unsigned long displayInterval = 2500;

void loop() {
  // Handle HTTP requests from mobile app
  server.handleClient();
  
  // WiFi reconnect
  if (!wifiOK && (millis() % 30000) < 200) {
    WiFi.reconnect();
    wifiOK = (WiFi.status() == WL_CONNECTED);
    digitalWrite(LED_WIFI, wifiOK ? HIGH : LOW);
  }

  // Sensor reading
  if (millis() - lastSensorRead >= sensorInterval) {
    updateSensors();
    updateLEDsAndAlerts();
    lastSensorRead = millis();
  }

  // Display update
  if (millis() - lastDisplayUpdate >= displayInterval) {
    updateDisplay();
    lastDisplayUpdate = millis();
  }

  // Send to server
  if (millis() - lastSend >= sendInterval) {
    sendToServer();
    lastSend = millis();
  }
}
