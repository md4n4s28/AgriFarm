package com.agrifarm.app.presentation.myfarm

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrifarm.app.data.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CompleteFarmScreen(
    viewModel: MyFarmViewModel2 = hiltViewModel()
) {
    val cropSession by viewModel.cropSession.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val pumpControl by viewModel.pumpControl.collectAsState()
    val weatherAlerts by viewModel.weatherAlerts.collectAsState()
    val landInfo by viewModel.landInfo.collectAsState()
    val cropSuggestions by viewModel.cropSuggestions.collectAsState()
    val resourceUsage by viewModel.resourceUsage.collectAsState()
    val cropComparisons by viewModel.cropComparisons.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Schedule", "Resources", "Suggestions")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "My Farm",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Complete farm management",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50),
                    edgePadding = 20.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
        
        when (selectedTab) {
            0 -> OverviewTab(cropSession, weatherAlerts, landInfo, pumpControl, viewModel)
            1 -> ScheduleTab(schedules, cropSession, viewModel)
            2 -> ResourcesTab(resourceUsage, cropComparisons)
            3 -> SuggestionsTab(cropSuggestions, landInfo)
        }
    }
}

@Composable
fun OverviewTab(
    cropSession: CropSessionDetail,
    weatherAlerts: List<WeatherAlert>,
    landInfo: LandInfo,
    pumpControl: PumpControl,
    viewModel: MyFarmViewModel2
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        CropSessionCard(cropSession)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PumpControlCard(pumpControl, viewModel)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (weatherAlerts.isNotEmpty()) {
            Text(
                text = "Weather Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            weatherAlerts.forEach { alert ->
                WeatherAlertCard(alert)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        LandInfoCard(landInfo)
    }
}

@Composable
fun CropSessionCard(cropSession: CropSessionDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cropSession.cropName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${cropSession.cropType} • ${cropSession.variety}",
                        fontSize = 14.sp,
                        color = Color.White.copy(0.9f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Grass,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Day ${cropSession.daysElapsed}",
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    icon = Icons.Outlined.Timer,
                    label = "${cropSession.daysRemaining}d left",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Growth Progress",
                        fontSize = 13.sp,
                        color = Color.White.copy(0.9f)
                    )
                    Text(
                        text = "${(cropSession.daysElapsed * 100 / (cropSession.daysElapsed + cropSession.daysRemaining))}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = cropSession.daysElapsed.toFloat() / (cropSession.daysElapsed + cropSession.daysRemaining),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(0.3f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Stage",
                        fontSize = 12.sp,
                        color = Color.White.copy(0.8f)
                    )
                    Text(
                        text = cropSession.currentStage.name.replace("_", " "),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Health",
                        fontSize = 12.sp,
                        color = Color.White.copy(0.8f)
                    )
                    Text(
                        text = cropSession.healthStatus,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Area",
                        fontSize = 12.sp,
                        color = Color.White.copy(0.8f)
                    )
                    Text(
                        text = "${cropSession.landArea} acres",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Outlined.Event, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Timeline")
                }
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Outlined.Analytics, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analytics")
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PumpControlCard(pumpControl: PumpControl, viewModel: MyFarmViewModel2) {
    var showScheduleDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (pumpControl.isOn) Color(0xFF2196F3).copy(0.1f) else Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = if (pumpControl.isOn) Color(0xFF2196F3) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Water Pump",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = pumpControl.mode.name,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Switch(
                    checked = pumpControl.isOn,
                    onCheckedChange = { viewModel.togglePump() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2196F3)
                    )
                )
            }
            
            if (pumpControl.isOn) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2196F3).copy(0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pump is running • ${pumpControl.waterUsed.toInt()} L used",
                            fontSize = 13.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = pumpControl.mode == PumpMode.MANUAL,
                    onClick = { viewModel.setPumpMode(PumpMode.MANUAL) },
                    label = { Text("Manual") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = pumpControl.mode == PumpMode.SCHEDULED,
                    onClick = { showScheduleDialog = true },
                    label = { Text("Schedule") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = pumpControl.mode == PumpMode.AUTO,
                    onClick = { viewModel.setPumpMode(PumpMode.AUTO) },
                    label = { Text("Auto") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (pumpControl.mode == PumpMode.SCHEDULED && pumpControl.scheduledStartTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Start Time", fontSize = 12.sp, color = Color.Gray)
                        Text(pumpControl.scheduledStartTime, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Column {
                        Text("End Time", fontSize = 12.sp, color = Color.Gray)
                        Text(pumpControl.scheduledEndTime, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
    
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Schedule Pump") },
            text = {
                Column {
                    Text("Set pump schedule times")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = "06:00 AM",
                        onValueChange = {},
                        label = { Text("Start Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "08:00 AM",
                        onValueChange = {},
                        label = { Text("End Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.schedulePump("06:00 AM", "08:00 AM")
                    showScheduleDialog = false
                }) {
                    Text("Set Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WeatherAlertCard(alert: WeatherAlert) {
    val alertColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> Color(0xFFF44336)
        AlertSeverity.HIGH -> Color(0xFFFF5722)
        AlertSeverity.MEDIUM -> Color(0xFFFF9800)
        AlertSeverity.LOW -> Color(0xFFFFC107)
        AlertSeverity.INFO -> Color(0xFF2196F3)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = alertColor.copy(0.1f))
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
                    .background(alertColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (alert.type) {
                        AlertType.HEAVY_RAIN -> Icons.Outlined.Cloud
                        AlertType.DROUGHT -> Icons.Outlined.WbSunny
                        AlertType.FROST -> Icons.Outlined.AcUnit
                        AlertType.HEATWAVE -> Icons.Outlined.Whatshot
                        AlertType.STORM -> Icons.Outlined.Thunderstorm
                        else -> Icons.Outlined.Notifications
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = alertColor
                )
                Text(
                    text = alert.message,
                    fontSize = 13.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (alert.actionRequired.isNotEmpty()) {
                    Text(
                        text = "Action: ${alert.actionRequired}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LandInfoCard(landInfo: LandInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Land Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Outlined.Landscape,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LandStatCard(
                    label = "Total",
                    value = "${landInfo.totalArea}",
                    unit = "acres",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                LandStatCard(
                    label = "Used",
                    value = "${landInfo.usedArea}",
                    unit = "acres",
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                LandStatCard(
                    label = "Available",
                    value = "${landInfo.availableArea}",
                    unit = "acres",
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Soil Type", fontSize = 12.sp, color = Color.Gray)
                    Text(landInfo.soilType, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("pH Level", fontSize = 12.sp, color = Color.Gray)
                    Text("${landInfo.phLevel}", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = landInfo.location,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun LandStatCard(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(0.1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                fontSize = 11.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
