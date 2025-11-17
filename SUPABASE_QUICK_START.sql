-- ============================================
-- QUICK START - Create Test User & Device
-- Run this FIRST to test IoT without auth
-- ============================================

-- Step 1: Temporarily allow NULL user_id for testing
ALTER TABLE public.iot_devices ALTER COLUMN user_id DROP NOT NULL;

-- Step 2: Register ESP32 device (without user for now)
INSERT INTO public.iot_devices (device_id, device_name, status)
VALUES (
    'ESP32_FARM_001',
    'Farm Sensor Unit 1',
    'offline'
)
ON CONFLICT (device_id) DO UPDATE 
SET device_name = EXCLUDED.device_name
RETURNING id, device_id;  -- Copy the device UUID!

-- Step 3: Test insert sensor data (replace DEVICE_UUID with the one from step 2)
INSERT INTO public.iot_sensor_data (device_id, temperature, humidity, soil_moisture, gas_level, rain_detected)
VALUES (
    (SELECT id FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001'),
    28.5,
    65.2,
    2450,
    1850,
    false
);

-- Step 4: Verify data
SELECT * FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001';
SELECT * FROM public.iot_sensor_data ORDER BY timestamp DESC LIMIT 5;

-- Step 5: Test functions
SELECT * FROM get_latest_sensor_data('ESP32_FARM_001');
SELECT is_device_online('ESP32_FARM_001');

-- ============================================
-- SUCCESS! Device registered without auth
-- ============================================
-- Copy the device UUID from Step 2 for ESP32 code
