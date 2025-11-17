package com.agrifarm.app.presentation.iot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

data class SensorConfig(
    val id: String,
    val name: String,
    val quantity: Int = 1,
    val distance: Int = 0,
    val onlineCount: Int = 1,
    val failedSensors: List<Int> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IotPackageSelectionScreen(
    onBackClick: () -> Unit,
    viewModel: com.agrifarm.app.presentation.myfarm.MyFarmViewModel = hiltViewModel()
) {
    val deviceStatus by viewModel.deviceStatus.collectAsState()
    val sensorData = deviceStatus.latestData
    
    /*data class SensorConfig(
        val id: String,
        val name: String,
        val quantity: Int = 1,
        val distance: Int = 0,
        val onlineCount: Int = 1,
        val failedSensors: List<Int> = emptyList()
    )*/
    
    val availableSensors = listOf(
        "Temperature & Humidity (DHT11)" to "dht",
        "Soil Moisture Sensor" to "soil",
        "Rain Detection Sensor" to "rain",
        "Gas/Air Quality Sensor" to "gas",
        "Light Intensity Sensor" to "light",
        "pH Level Sensor" to "ph",
        "NPK Sensor (N-P-K)" to "npk",
        "Water Level Sensor" to "water"
    )
    
    var sensorConfigs by remember { 
        mutableStateOf(mapOf(
            "dht" to SensorConfig("dht", "Temperature & Humidity", 2, 10, 2),
            "soil" to SensorConfig("soil", "Soil Moisture", 3, 15, 2, listOf(3)),
            "rain" to SensorConfig("rain", "Rain Sensor", 1, 0, 1),
            "gas" to SensorConfig("gas", "Gas Sensor", 2, 20, 2)
        ))
    }
    var expanded by remember { mutableStateOf(false) }
    var showConfig by remember { mutableStateOf(false) }
    var showEspConfig by remember { mutableStateOf(false) }
    var editingSensor by remember { mutableStateOf<String?>(null) }
    
    editingSensor?.let { sensorId ->
        val config = sensorConfigs[sensorId]
        if (config != null) {
            var quantity by remember { mutableStateOf(config.quantity.toString()) }
            var distance by remember { mutableStateOf(config.distance.toString()) }
            
            AlertDialog(
                onDismissRequest = { editingSensor = null },
                title = { Text("Configure ${config.name}") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                            label = { Text("Number of Sensors") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = distance,
                            onValueChange = { distance = it.filter { c -> c.isDigit() } },
                            label = { Text("Distance Between Sensors (meters)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val qty = quantity.toIntOrNull() ?: 1
                        val dist = distance.toIntOrNull() ?: 0
                        sensorConfigs = sensorConfigs + (sensorId to config.copy(
                            quantity = qty.coerceIn(1, 10),
                            distance = dist.coerceIn(0, 100),
                            onlineCount = qty
                        ))
                        editingSensor = null
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingSensor = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "IoT Monitoring",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            "Real-time sensor dashboard",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showEspConfig = !showEspConfig },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD))
                        ) {
                            Icon(Icons.Outlined.Wifi, null, tint = Color(0xFF2196F3))
                        }
                        IconButton(
                            onClick = { showConfig = !showConfig },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                        ) {
                            Icon(Icons.Outlined.Tune, null, tint = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
            
            Column(modifier = Modifier.padding(20.dp)) {
                if (showEspConfig) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ESP32 Device Settings",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                TextButton(onClick = { showEspConfig = false }) {
                                    Text("Done", color = Color(0xFF2196F3))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            var espIp by remember { mutableStateOf(viewModel.getEspIp()) }
                            
                            OutlinedTextField(
                                value = espIp,
                                onValueChange = { espIp = it },
                                label = { Text("ESP32 IP Address", fontSize = 12.sp) },
                                placeholder = { Text("192.168.x.x", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Router, null, tint = Color(0xFF2196F3))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2196F3),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    viewModel.updateEspIp(espIp)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.Save, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save & Reconnect")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Current IP: ${viewModel.getEspIp()}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (showConfig) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sensor Configuration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { showConfig = false }) {
                                    Text("Done", color = Color(0xFF4CAF50))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Configured Sensors", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            sensorConfigs.forEach { (id, config) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(config.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                            Text("Qty: ${config.quantity} | Distance: ${config.distance}m", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Row {
                                            TextButton(onClick = { editingSensor = id }) {
                                                Text("Edit", fontSize = 12.sp)
                                            }
                                            TextButton(onClick = { 
                                                sensorConfigs = sensorConfigs - id
                                            }) {
                                                Text("Remove", fontSize = 12.sp, color = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = "Select Sensors (${sensorConfigs.size} selected)",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    availableSensors.forEach { (name, id) ->
                                        val isSelected = sensorConfigs.containsKey(id)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    if (isSelected) {
                                                        sensorConfigs = sensorConfigs - id
                                                    } else {
                                                        sensorConfigs = sensorConfigs + (id to SensorConfig(id, name, 1, 0, 1))
                                                        editingSensor = id
                                                        expanded = false
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = null,
                                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(name, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        value = "${sensorConfigs.values.sumOf { it.quantity }}",
                        label = "Total Sensors",
                        icon = Icons.Outlined.Sensors,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        value = if (deviceStatus.isOnline) "Online" else "Offline",
                        label = "Device Status",
                        icon = Icons.Outlined.Wifi,
                        color = if (deviceStatus.isOnline) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        value = "${sensorData.temperature.toInt()}°C",
                        label = "Temperature",
                        icon = Icons.Outlined.Thermostat,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        value = "${sensorData.soilMoisturePct}%",
                        label = "Soil Moisture",
                        icon = Icons.Outlined.WaterDrop,
                        color = if (sensorData.soilMoisturePct > 50) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (!deviceStatus.isOnline) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.WifiOff, null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Device Offline",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    "Showing last saved data. Reconnect ESP32 for live updates.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Text("Sensor Readings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                sensorConfigs["dht"]?.let { config ->
                    val dhtReadings = if (sensorData.dhtSensors.isNotEmpty()) {
                        sensorData.dhtSensors
                    } else {
                        // Create placeholder sensors based on config quantity when no data
                        List(config.quantity) { index ->
                            com.agrifarm.app.data.model.DHTReading(
                                id = index + 1,
                                temperature = if (index == 0) sensorData.temperature else 0f,
                                humidity = if (index == 0) sensorData.humidity else 0f,
                                isOnline = if (index == 0) sensorData.dhtOk else false
                            )
                        }
                    }
                    val onlineSensors = dhtReadings.filter { it.isOnline }
                    val avgTemp = if (onlineSensors.isNotEmpty()) onlineSensors.map { it.temperature }.average().toFloat() else 0f
                    val avgHum = if (onlineSensors.isNotEmpty()) onlineSensors.map { it.humidity }.average().toFloat() else 0f
                    val failedIds = dhtReadings.filter { !it.isOnline }.map { it.id }
                    val onlineCount = dhtReadings.count { it.isOnline }
                    
                    MultiSensorReadingCard(
                        title = "Temperature & Humidity",
                        icon = Icons.Outlined.Thermostat,
                        color = Color(0xFF2196F3),
                        config = config.copy(quantity = dhtReadings.size, onlineCount = onlineCount, failedSensors = failedIds),
                        readings = listOf(
                            "Avg Temperature" to "${if (avgTemp.isNaN()) 0f else avgTemp}°C",
                            "Avg Humidity" to "${if (avgHum.isNaN()) 0f else avgHum}%"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["soil"]?.let { config ->
                    val soilReadings = if (sensorData.soilSensors.isNotEmpty()) {
                        sensorData.soilSensors
                    } else {
                        List(config.quantity) { index ->
                            com.agrifarm.app.data.model.SoilReading(
                                id = index + 1,
                                moisture = if (index == 0) sensorData.soilMoisture else 0,
                                moisturePct = if (index == 0) sensorData.soilMoisturePct else 0,
                                isOnline = if (index == 0) sensorData.soilOk else false
                            )
                        }
                    }
                    val onlineSensors = soilReadings.filter { it.isOnline }
                    val avgMoisture = if (onlineSensors.isNotEmpty()) onlineSensors.map { it.moisturePct }.average().toInt() else 0
                    val failedIds = soilReadings.filter { !it.isOnline }.map { it.id }
                    val onlineCount = soilReadings.count { it.isOnline }
                    
                    MultiSensorReadingCard(
                        title = "Soil Moisture",
                        icon = Icons.Outlined.Grass,
                        color = Color(0xFF4CAF50),
                        config = config.copy(quantity = soilReadings.size, onlineCount = onlineCount, failedSensors = failedIds),
                        readings = listOf(
                            "Avg Moisture" to "$avgMoisture%",
                            "Coverage" to "${config.distance * soilReadings.size}m"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["rain"]?.let { config ->
                    val rainReadings = if (sensorData.rainSensors.isNotEmpty()) {
                        sensorData.rainSensors
                    } else {
                        List(config.quantity) { index ->
                            com.agrifarm.app.data.model.RainReading(
                                id = index + 1,
                                detected = if (index == 0) sensorData.rainDetected else false,
                                analog = if (index == 0) sensorData.rainAnalog else 0,
                                isOnline = if (index == 0) sensorData.rainOk else false
                            )
                        }
                    }
                    val onlineSensors = rainReadings.filter { it.isOnline }
                    val anyRainDetected = onlineSensors.any { it.detected }
                    val failedIds = rainReadings.filter { !it.isOnline }.map { it.id }
                    val onlineCount = rainReadings.count { it.isOnline }
                    
                    MultiSensorReadingCard(
                        title = "Rain Sensor",
                        icon = Icons.Outlined.Cloud,
                        color = Color(0xFF00BCD4),
                        config = config.copy(quantity = rainReadings.size, onlineCount = onlineCount, failedSensors = failedIds),
                        readings = listOf(
                            "Status" to if (anyRainDetected) "Rain Detected" else "No Rain",
                            "Sensors Online" to "$onlineCount/${rainReadings.size}"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["gas"]?.let { config ->
                    val gasReadings = if (sensorData.gasSensors.isNotEmpty()) {
                        sensorData.gasSensors
                    } else {
                        List(config.quantity) { index ->
                            com.agrifarm.app.data.model.GasReading(
                                id = index + 1,
                                level = if (index == 0) sensorData.gasLevel else 0,
                                isOnline = if (index == 0) sensorData.gasOk else false
                            )
                        }
                    }
                    val onlineSensors = gasReadings.filter { it.isOnline }
                    val avgGas = if (onlineSensors.isNotEmpty()) onlineSensors.map { it.level }.average().toInt() else 0
                    val failedIds = gasReadings.filter { !it.isOnline }.map { it.id }
                    val onlineCount = gasReadings.count { it.isOnline }
                    
                    MultiSensorReadingCard(
                        title = "Gas Sensor",
                        icon = Icons.Outlined.Air,
                        color = Color(0xFFFF9800),
                        config = config.copy(quantity = gasReadings.size, onlineCount = onlineCount, failedSensors = failedIds),
                        readings = listOf(
                            "Avg Gas Level" to "$avgGas",
                            "Status" to when {
                                avgGas > 3600 -> "Critical"
                                avgGas > 3000 -> "Warning"
                                else -> "Normal"
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["light"]?.let { config ->
                    MultiSensorReadingCard(
                        title = "Light Intensity",
                        icon = Icons.Outlined.WbSunny,
                        color = Color(0xFFFFC107),
                        config = config,
                        readings = listOf(
                            "Avg Intensity" to "850 Lux",
                            "Status" to "Optimal"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["ph"]?.let { config ->
                    MultiSensorReadingCard(
                        title = "Soil pH Level",
                        icon = Icons.Outlined.Science,
                        color = Color(0xFF9C27B0),
                        config = config,
                        readings = listOf(
                            "Avg pH Value" to "6.5",
                            "Status" to "Slightly Acidic"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["npk"]?.let { config ->
                    MultiSensorReadingCard(
                        title = "NPK Nutrients",
                        icon = Icons.Outlined.Eco,
                        color = Color(0xFF4CAF50),
                        config = config,
                        readings = listOf(
                            "Avg Nitrogen (N)" to "45 mg/kg",
                            "Avg Phosphorus (P)" to "32 mg/kg",
                            "Avg Potassium (K)" to "38 mg/kg"
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                sensorConfigs["water"]?.let { config ->
                    MultiSensorReadingCard(
                        title = "Water Level",
                        icon = Icons.Outlined.WaterDrop,
                        color = Color(0xFF00BCD4),
                        config = config,
                        readings = listOf(
                            "Avg Level" to "75%",
                            "Total Volume" to "450 Liters"
                        )
                    )
                }
            }
    }
}

@Composable
fun QuickStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
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
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SensorReadingCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isWorking: Boolean,
    readings: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isWorking) color else Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWorking) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(0.6f))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            readings.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 14.sp, color = Color.Gray)
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MultiSensorReadingCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    config: SensorConfig,
    readings: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${config.quantity} sensors • ${config.distance}m spacing", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sensor Status", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${config.onlineCount}/${config.quantity} Online", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(config.quantity) { index ->
                    val isOnline = !config.failedSensors.contains(index + 1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isOnline) color else Color.Gray)
                    )
                }
            }
            
            if (config.failedSensors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Warning, null, tint = Color(0xFFF44336), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Failed: Sensor #${config.failedSensors.joinToString(", #")}",
                        fontSize = 12.sp,
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            
            readings.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 14.sp, color = Color.Gray)
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
