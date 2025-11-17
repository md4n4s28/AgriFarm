// -----------------------------------------------------------
// SMART AGRI IoT – SENSOR-ROBUST VERSION
// Adds sensor-fault detection + ADC stabilization + safe display
// Compatible with your wiring (SN74HC595, OLED, DHT11, AO/DO sensors)
// -----------------------------------------------------------

#include <WiFi.h>
#include <HTTPClient.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>
#include "DHT.h"

// ---------------- WiFi / API ----------------
const char* ssid = "Moto";
const char* password = "anas@1234";
String apiURL = "https://eob86s60nw99fn5.m.pipedream.net";

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
#define GAS_A  35
#define BUZZER 5

// 74HC595 pins
#define DATA_PIN   23
#define LATCH_PIN  19
#define CLOCK_PIN  18

// LED bit indexes on 595
#define LED_SOIL_HEALTH   0
#define LED_DHT_HEALTH    1
#define LED_GAS_HEALTH    2
#define LED_RAIN_HEALTH   3
#define LED_WARNING       4
#define LED_CRITICAL      5
#define LED_SOIL_DRY      6
#define LED_SYSTEM_OK     7

// direct WiFi LED
#define LED_WIFI 13

// ---------------- variables ----------------
unsigned long lastSend = 0;
unsigned long sendInterval = 20000;

float hum = NAN;
float tempC = NAN;
int soilAnalog = -1, gasVal = -1, rainAnalog = -1;
bool soilDO = false, rainDO = false;
bool soilOK=false, gasOK=false, rainOK=false, dhtOK=false;
bool wifiOK=false;
byte ledBits = 0;

// ADC config
void adcSetup() {
  analogSetPinAttenuation(SOIL_A, ADC_11db);
  analogSetPinAttenuation(GAS_A, ADC_11db);
  analogSetPinAttenuation(RAIN_A, ADC_11db);
  analogSetWidth(12); // 0..4095
}

// ---------------- helpers ----------------

// Read analog pin N samples, return average. Detect open sensor (float) if all samples ~ 0 or ~4095
int readAnalogStable(int pin, int samples=5, int delayMs=5) {
  int minV = 4096, maxV = -1;
  long sum = 0;
  for (int i=0;i<samples;i++) {
    int v = analogRead(pin);
    sum += v;
    if (v < minV) minV = v;
    if (v > maxV) maxV = v;
    delay(delayMs);
  }
  int avg = sum / samples;
  // If the readings are stuck at extremes or no variation, treat as disconnected
  if ( (maxV - minV) < 3 ) { // almost constant reading
    // if nearly zero or nearly full-scale -> suspicious/disconnected
    if (avg < 10 || avg > 4085) return -1;
  }
  return avg;
}

// safe DHT read with retry
float safeReadHumidity() {
  float h = dht.readHumidity();
  if (!isnan(h) && h >= 0.0 && h <= 100.0) return h;
  // retry once quickly
  delay(200);
  h = dht.readHumidity();
  if (!isnan(h) && h >= 0.0 && h <= 100.0) return h;
  return NAN;
}

// ---------------- shift register ----------------
void shiftOutData(byte data) {
  digitalWrite(LATCH_PIN, LOW);
  shiftOut(DATA_PIN, CLOCK_PIN, MSBFIRST, data);
  digitalWrite(LATCH_PIN, HIGH);
}

// ---------------- setup ----------------
void setup() {
  Serial.begin(115200);
  adcSetup();

  // pins
  pinMode(SOIL_D, INPUT);
  pinMode(RAIN_D, INPUT);
  pinMode(BUZZER, OUTPUT);
  pinMode(LED_WIFI, OUTPUT);

  pinMode(DATA_PIN, OUTPUT);
  pinMode(LATCH_PIN, OUTPUT);
  pinMode(CLOCK_PIN, OUTPUT);
  shiftOutData(0x00);

  // OLED
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("OLED init fail");
    for(;;);
  }
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0,0);
  display.println("Booting...");
  display.display();

  dht.begin();
  delay(200); // brief settle

  // WiFi (non-blocking attempt, but we'll wait small time)
  WiFi.begin(ssid, password);
  int t = 0;
  while (WiFi.status() != WL_CONNECTED && t < 15) { delay(300); Serial.print("."); t++; }
  wifiOK = (WiFi.status() == WL_CONNECTED);
  digitalWrite(LED_WIFI, wifiOK ? HIGH : LOW);

  Serial.println();
  Serial.println("Setup done. Sensors OK list: (will detect)");
}

// ---------------- update sensors ----------------
void updateSensors() {
  // DHT humidity only (user wanted temp not shown)
  hum = safeReadHumidity();
  dhtOK = !isnan(hum);

  // analogs: read stable average and detect disconnected (-1)
  int s = readAnalogStable(SOIL_A);
  if (s < 0) { soilAnalog = -1; soilOK = false; } else { soilAnalog = s; soilOK = true; }
  int g = readAnalogStable(GAS_A);
  if (g < 0) { gasVal = -1; gasOK = false; } else { gasVal = g; gasOK = true; }
  int r = readAnalogStable(RAIN_A);
  if (r < 0) { rainAnalog = -1; rainOK = false; } else { rainAnalog = r; rainOK = true; }

  // DO pins (digital) - ensure using INPUT_PULLUP if module floats; many modules pull low when wet
  pinMode(SOIL_D, INPUT_PULLUP);
  pinMode(RAIN_D, INPUT_PULLUP);
  soilDO = digitalRead(SOIL_D); // 1=not-wet often, 0=wet often (depends on module)
  rainDO = digitalRead(RAIN_D);
}

// ---------------- LED logic ----------------
void updateLEDsAndAlerts() {
  byte bits = 0;
  bool anyDanger = false;
  bool anyWarning = false;

  // Health green LEDs: set if sensor reading is valid
  if (dhtOK) bits |= (1<<LED_DHT_HEALTH);
  if (soilOK) bits |= (1<<LED_SOIL_HEALTH);
  if (gasOK) bits |= (1<<LED_GAS_HEALTH);
  if (rainOK) bits |= (1<<LED_RAIN_HEALTH);

  // Soil dryness logic (calibrate thresholds per field)
  if (soilOK && soilAnalog >= 0) {
    // Example thresholds (tune by testing): >3500 very dry, 3000 warning
    if (soilAnalog > 3500) {
      bits |= (1<<LED_SOIL_DRY); anyDanger = false; anyWarning = true;
    } else if (soilAnalog > 3000) {
      bits |= (1<<LED_SOIL_DRY); anyWarning = true;
    }
  }

  // Gas logic
  if (gasOK && gasVal >= 0) {
    if (gasVal > 3600) {
      bits |= (1<<LED_CRITICAL); anyDanger = true;
    } else if (gasVal > 3000) {
      bits |= (1<<LED_CRITICAL); anyWarning = true;
    }
  }

  // Rain: show warning LED (or treat as normal)
  if (rainOK && rainAnalog >= 0) {
    // if DO indicates wet (module may be active-low)
    if (rainDO == LOW) { bits |= (1<<LED_WARNING); anyWarning = true; }
  }

  if (!anyDanger && !anyWarning) bits |= (1<<LED_SYSTEM_OK);

  // push out
  shiftOutData(bits);
  ledBits = bits;

  // buzzer: only for real danger, non-blocking simple approach
  if (anyDanger) {
    tone(BUZZER, 1200); // continuous attention tone
  } else {
    noTone(BUZZER);
    // optional: for warnings do a single short beep (but throttle)
    static unsigned long lastWarn = 0;
    if (anyWarning && millis() - lastWarn > 15000) {
      tone(BUZZER, 2000, 120);
      lastWarn = millis();
    }
  }
}

// ---------------- display ----------------
void updateDisplay() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setCursor(0,0);

  // Humidity
  if (dhtOK) display.printf("Humidity: %.1f %%\n", hum);
  else display.println("Humidity: N/A");

  // Soil ADC
  if (soilOK) display.printf("Soil ADC: %d\n", soilAnalog);
  else display.println("Soil: N/A (probe?)");

  // Gas
  if (gasOK) display.printf("Gas ADC: %d\n", gasVal);
  else display.println("Gas: N/A");

  // Rain
  if (rainOK) display.printf("Rain ADC: %d DO:%d\n", rainAnalog, rainDO);
  else display.println("Rain: N/A");

  display.printf("WiFi: %s\n", wifiOK ? "OK" : "NO");
  display.display();
}

// ---------------- send to server ----------------
void sendToServer() {
  if (!wifiOK) return;

  HTTPClient http;
  http.begin(apiURL);
  http.addHeader("Content-Type", "application/json");
  String json = "{";
  json += "\"humidity\":" + String(dhtOK ? hum : -1) + ",";
  json += "\"soilADC\":" + String(soilOK ? soilAnalog : -1) + ",";
  json += "\"gasADC\":" + String(gasOK ? gasVal : -1) + ",";
  json += "\"rainADC\":" + String(rainOK ? rainAnalog : -1) + ",";
  json += "\"rainDO\":" + String(rainDO ? 1 : 0);
  json += "}";
  int code = http.POST(json);
  Serial.println(code > 0 ? "Sent: " + String(code) : "HTTP Err: " + String(code));
  http.end();
}

// ---------------- loop ----------------
unsigned long lastLoopRead = 0;
unsigned long loopIntervalRead = 2000;
unsigned long lastLoopDisp = 0;
unsigned long dispInterval = 2500;

void loop() {
  // attempt WiFi reconnect occasionally
  if (!wifiOK && (millis() % 15000) < 200) {
    WiFi.reconnect();
    wifiOK = (WiFi.status() == WL_CONNECTED);
    digitalWrite(LED_WIFI, wifiOK ? HIGH : LOW);
  }

  // periodic sensor read
  if (millis() - lastLoopRead >= loopIntervalRead) {
    updateSensors();
    updateLEDsAndAlerts();
    lastLoopRead = millis();
  }

  if (millis() - lastLoopDisp >= dispInterval) {
    updateDisplay();
    lastLoopDisp = millis();
  }

  if (millis() - lastSend >= sendInterval) {
    sendToServer();
    lastSend = millis();
  }
}
