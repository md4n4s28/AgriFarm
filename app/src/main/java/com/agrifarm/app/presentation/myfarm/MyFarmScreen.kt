package com.agrifarm.app.presentation.myfarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyFarmScreen(
    viewModel: MyFarmViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val deviceStatus by viewModel.deviceStatus.collectAsState()
    val trends by viewModel.trends.collectAsState()
    val sensorData = deviceStatus.latestData
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "My Farm",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "IoT sensor monitoring",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
        
        Column(modifier = Modifier.padding(20.dp)) {
            // Device Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {

            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            if (sensorData.ledCritical) {
                SmartAlert(
                    icon = Icons.Outlined.Warning,
                    title = "🔴 CRITICAL: High Gas Level!",
                    message = "Gas level is ${sensorData.gasLevel} (Critical > 3600). Ventilate area immediately! Red LED is ON.",
                    color = Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (sensorData.ledSoilDry) {
                SmartAlert(
                    icon = Icons.Outlined.WaterDrop,
                    title = "⚪ Soil is Critically Dry!",
                    message = "Soil moisture is ${sensorData.soilMoisturePct}% (${sensorData.soilMoisture}). White LED ON - Irrigation needed urgently!",
                    color = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (sensorData.ledWarning && !sensorData.ledSoilDry && !sensorData.ledCritical) {
                val warningMsg = when {
                    sensorData.rainDetected -> "Rain detected! Yellow LED ON - Skip irrigation."
                    sensorData.gasLevel > 3000 -> "Gas level ${sensorData.gasLevel} is elevated. Yellow LED ON - Monitor closely."
                    else -> "Yellow LED ON - Check sensors."
                }
                SmartAlert(
                    icon = Icons.Outlined.Notifications,
                    title = "🟡 Warning Alert",
                    message = warningMsg,
                    color = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            if (sensorData.ledSystemOk && !sensorData.ledCritical && !sensorData.ledWarning && !sensorData.ledSoilDry) {
                SmartAlert(
                    icon = Icons.Outlined.CheckCircle,
                    title = "🟢 All Systems Normal",
                    message = "Green LED ON - All sensors working properly. No action needed.",
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Device Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Sensors,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AgriFarm Device",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (deviceStatus.isOnline) Color(0xFF4CAF50) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (deviceStatus.isOnline) "Online" else "Offline",
                                    fontSize = 12.sp,
                                    color = if (deviceStatus.isOnline) Color(0xFF4CAF50) else Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Text(
                        text = deviceStatus.lastSeen,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Sensor Readings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Sensors Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SensorCard(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Soil Moisture",
                    value = "${sensorData.soilMoisturePct}",
                    unit = "%",
                    status = if (sensorData.soilMoisturePct > 50) "Good" else "Low",
                    statusColor = if (sensorData.soilMoisturePct > 50) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    icon = Icons.Outlined.Thermostat,
                    label = "Temperature",
                    value = "${sensorData.temperature.toInt()}",
                    unit = "°C",
                    status = "Normal",
                    statusColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SensorCard(
                    icon = Icons.Outlined.Air,
                    label = "Humidity",
                    value = "${sensorData.humidity.toInt()}",
                    unit = "%",
                    status = "Optimal",
                    statusColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    icon = Icons.Outlined.Cloud,
                    label = "Rain",
                    value = if (sensorData.rainDetected) "Yes" else "No",
                    unit = "",
                    status = if (sensorData.rainDetected) "Detected" else "Dry",
                    statusColor = if (sensorData.rainDetected) Color(0xFF2196F3) else Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SensorCard(
                    icon = Icons.Outlined.Science,
                    label = "Gas Level",
                    value = "${sensorData.gasLevel}",
                    unit = "ADC",
                    status = if (sensorData.gasLevel < 3000) "Safe" else "Alert",
                    statusColor = if (sensorData.gasLevel < 3000) Color(0xFF4CAF50) else Color(0xFFFF5722),
                    modifier = Modifier.weight(1f)
                )
                SensorCard(
                    icon = Icons.Outlined.BatteryFull,
                    label = "Battery",
                    value = "85",
                    unit = "%",
                    status = "Good",
                    statusColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "24-Hour Trends",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (trends.isNotEmpty()) {
                        val latest = trends.lastOrNull()
                        val previous = trends.getOrNull(trends.size - 2)
                        if (latest != null && previous != null) {
                            val soilChange = latest.avgSoilMoisturePct - previous.avgSoilMoisturePct
                            val tempChange = latest.avgTemperature - previous.avgTemperature
                            val humChange = latest.avgHumidity - previous.avgHumidity
                            
                            TrendItem(
                                "Soil Moisture",
                                if (soilChange > 0) "↑ ${soilChange}%" else if (soilChange < 0) "↓ ${-soilChange}%" else "→ Stable",
                                if (soilChange > 0) Color(0xFF4CAF50) else if (soilChange < 0) Color(0xFFF44336) else Color.Gray
                            )
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
                            TrendItem(
                                "Temperature",
                                if (tempChange > 0) "↑ %.1f°C".format(tempChange) else if (tempChange < 0) "↓ %.1f°C".format(-tempChange) else "→ Stable",
                                if (tempChange > 0) Color(0xFF4CAF50) else if (tempChange < 0) Color(0xFF2196F3) else Color.Gray
                            )
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
                            TrendItem(
                                "Humidity",
                                if (humChange > 0) "↑ %.1f%%".format(humChange) else if (humChange < 0) "↓ %.1f%%".format(-humChange) else "→ Stable",
                                if (humChange > 0) Color(0xFF4CAF50) else if (humChange < 0) Color(0xFFF44336) else Color.Gray
                            )
                        } else {
                            Text("Collecting trend data...", fontSize = 14.sp, color = Color.Gray)
                        }
                    } else {
                        Text("No trend data available", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AlertCard(
                icon = Icons.Outlined.Warning,
                title = "Low Soil Moisture",
                description = "Consider irrigation in next 24 hours",
                severity = "Medium"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AlertCard(
                icon = Icons.Outlined.CheckCircle,
                title = "Optimal Conditions",
                description = "Temperature and humidity are ideal",
                severity = "Info"
            )
        }
    }
}

@Composable
fun SensorCard(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = " $unit",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = status,
                fontSize = 11.sp,
                color = statusColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TrendItem(label: String, change: String, changeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = change,
            fontSize = 14.sp,
            color = changeColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DetailedLEDRow(label: String, isOn: Boolean, color: Color, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isOn) color else Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (isOn) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.6f))
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = if (isOn) status else "Off",
                    fontSize = 12.sp,
                    color = if (isOn) color else Color.Gray
                )
            }
        }
        Text(
            text = if (isOn) "ON" else "OFF",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOn) color else Color.Gray
        )
    }
}

@Composable
fun LEDIndicator(label: String, isOn: Boolean, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isOn) color else Color.Gray.copy(0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (isOn) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.5f))
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isOn) color else Color.Gray
        )
    }
}

@Composable
fun SmartAlert(
    icon: ImageVector,
    title: String,
    message: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(0.1f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AlertCard(
    icon: ImageVector,
    title: String,
    description: String,
    severity: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = severity,
                fontSize = 11.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

