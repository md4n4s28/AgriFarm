-- ============================================
-- AGRIFARM IOT SENSOR FUNCTIONS
-- Add to your existing Supabase database
-- Run this in Supabase SQL Editor
-- ============================================

-- Your iot_sensor_data table already exists!
-- We just need to add helper functions

-- 1. Function to get latest sensor data from iot_sensor_data
CREATE OR REPLACE FUNCTION get_latest_sensor_data(p_device_id TEXT DEFAULT 'ESP32_FARM_001')
RETURNS TABLE (
    device_id TEXT,
    ts BIGINT,
    temperature FLOAT,
    humidity FLOAT,
    soil_moisture INT,
    soil_moisture_pct INT,
    gas_level INT,
    rain_detected BOOLEAN,
    rain_analog INT,
    dht_ok BOOLEAN,
    soil_ok BOOLEAN,
    gas_ok BOOLEAN,
    rain_ok BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        d.device_id::TEXT,
        EXTRACT(EPOCH FROM sd.timestamp)::BIGINT,
        sd.temperature::FLOAT,
        sd.humidity::FLOAT,
        sd.soil_moisture,
        CASE 
            WHEN sd.soil_moisture IS NOT NULL THEN ((4095 - sd.soil_moisture) * 100 / 4095)
            ELSE 0
        END AS soil_moisture_pct,
        sd.gas_level,
        COALESCE(sd.rain_detected, false),
        0 AS rain_analog,
        (sd.temperature IS NOT NULL) AS dht_ok,
        (sd.soil_moisture IS NOT NULL) AS soil_ok,
        (sd.gas_level IS NOT NULL) AS gas_ok,
        (sd.rain_detected IS NOT NULL) AS rain_ok
    FROM public.iot_sensor_data sd
    JOIN public.iot_devices d ON sd.device_id = d.id
    WHERE d.device_id = p_device_id
    ORDER BY sd.timestamp DESC
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- 2. Function to get 24-hour trends
CREATE OR REPLACE FUNCTION get_24h_sensor_trends(p_device_id TEXT DEFAULT 'ESP32_FARM_001')
RETURNS TABLE (
    hour_ago INT,
    avg_temperature FLOAT,
    avg_humidity FLOAT,
    avg_soil_moisture_pct INT,
    avg_gas_level INT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        EXTRACT(HOUR FROM NOW() - sd.timestamp)::INT as hour_ago,
        AVG(sd.temperature::FLOAT) as avg_temperature,
        AVG(sd.humidity::FLOAT) as avg_humidity,
        AVG(((4095 - COALESCE(sd.soil_moisture, 0)) * 100 / 4095))::INT as avg_soil_moisture_pct,
        AVG(sd.gas_level)::INT as avg_gas_level
    FROM public.iot_sensor_data sd
    JOIN public.iot_devices d ON sd.device_id = d.id
    WHERE d.device_id = p_device_id
        AND sd.timestamp >= NOW() - INTERVAL '24 hours'
    GROUP BY hour_ago
    ORDER BY hour_ago ASC;
END;
$$ LANGUAGE plpgsql;

-- 3. Function to check device online status (last 5 minutes)
CREATE OR REPLACE FUNCTION is_device_online(p_device_id TEXT DEFAULT 'ESP32_FARM_001')
RETURNS BOOLEAN AS $$
DECLARE
    last_seen TIMESTAMP WITH TIME ZONE;
BEGIN
    SELECT sd.timestamp INTO last_seen
    FROM public.iot_sensor_data sd
    JOIN public.iot_devices d ON sd.device_id = d.id
    WHERE d.device_id = p_device_id
    ORDER BY sd.timestamp DESC
    LIMIT 1;
    
    IF last_seen IS NULL THEN
        RETURN FALSE;
    END IF;
    
    RETURN (NOW() - last_seen) < INTERVAL '5 minutes';
END;
$$ LANGUAGE plpgsql;

-- 4. Update iot_devices status automatically (trigger)
CREATE OR REPLACE FUNCTION update_device_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE public.iot_devices
    SET status = 'online', last_sync = NEW.timestamp
    WHERE id = NEW.device_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_device_status ON public.iot_sensor_data;
CREATE TRIGGER trigger_update_device_status
    AFTER INSERT ON public.iot_sensor_data
    FOR EACH ROW
    EXECUTE FUNCTION update_device_status();

-- ============================================
-- TEST QUERIES (Run these to verify)
-- ============================================

-- Test 1: Get latest sensor data
-- SELECT * FROM get_latest_sensor_data('ESP32_FARM_001');

-- Test 2: Get 24-hour trends
-- SELECT * FROM get_24h_sensor_trends('ESP32_FARM_001');

-- Test 3: Check if device is online
-- SELECT is_device_online('ESP32_FARM_001');

-- Test 4: View all sensor data
-- SELECT * FROM public.iot_sensor_data ORDER BY timestamp DESC LIMIT 10;

-- ============================================
-- SETUP COMPLETE!
-- ============================================
-- Next steps:
-- 1. Update ESP32 code with your Supabase URL
-- 2. Register device in iot_devices table
-- 3. ESP32 will auto-insert to iot_sensor_data
-- 4. Android app will fetch using these functions
