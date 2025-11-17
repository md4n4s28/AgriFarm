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
import com.agrifarm.app.data.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleTab(
    schedules: List<Schedule>,
    cropSession: CropSessionDetail,
    viewModel: MyFarmViewModel2
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        HarvestCountdownCard(cropSession)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Upcoming Tasks",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        schedules.sortedBy { it.scheduledTime }.forEach { schedule ->
            ScheduleCard(schedule, viewModel)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(Icons.Outlined.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Task")
        }
    }
}

@Composable
fun HarvestCountdownCard(cropSession: CropSessionDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9800)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Agriculture,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${cropSession.daysRemaining}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Days Until Harvest",
                fontSize = 16.sp,
                color = Color.White.copy(0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Expected: ${formatDateFarm(cropSession.expectedHarvestDate)}",
                fontSize = 13.sp,
                color = Color.White.copy(0.8f)
            )
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule, viewModel: MyFarmViewModel2) {
    val scheduleColor = when (schedule.type) {
        ScheduleType.WATERING -> Color(0xFF2196F3)
        ScheduleType.FERTILIZER -> Color(0xFF4CAF50)
        ScheduleType.PESTICIDE -> Color(0xFFFF5722)
        ScheduleType.INSPECTION -> Color(0xFFFF9800)
        ScheduleType.HARVESTING -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isCompleted) Color(0xFFF5F5F5) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(scheduleColor.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (schedule.type) {
                        ScheduleType.WATERING -> Icons.Outlined.WaterDrop
                        ScheduleType.FERTILIZER -> Icons.Outlined.Eco
                        ScheduleType.PESTICIDE -> Icons.Outlined.Science
                        ScheduleType.INSPECTION -> Icons.Outlined.Visibility
                        ScheduleType.HARVESTING -> Icons.Outlined.Agriculture
                        else -> Icons.Outlined.Task
                    },
                    contentDescription = null,
                    tint = scheduleColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (schedule.isCompleted) Color.Gray else Color.Black
                )
                Text(
                    text = schedule.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatScheduleTime(schedule.scheduledTime),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    if (schedule.isRecurring) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = scheduleColor.copy(0.1f)
                        ) {
                            Text(
                                text = "Recurring",
                                fontSize = 10.sp,
                                color = scheduleColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Checkbox(
                checked = schedule.isCompleted,
                onCheckedChange = { viewModel.completeSchedule(schedule.id) },
                colors = CheckboxDefaults.colors(
                    checkedColor = scheduleColor
                )
            )
        }
    }
}

@Composable
fun ResourcesTab(
    resourceUsage: List<ResourceUsage>,
    cropComparisons: List<CropComparison>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Resource Consumption",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        resourceUsage.forEach { resource ->
            ResourceUsageCard(resource)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Crop Comparison",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Compare different crops based on resources & profitability",
            fontSize = 13.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        cropComparisons.forEach { comparison ->
            CropComparisonCard(comparison)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ResourceUsageCard(resource: ResourceUsage) {
    val progress = if (resource.planned > 0) resource.used / resource.planned else 0f
    val progressColor = when {
        progress < 0.5f -> Color(0xFF4CAF50)
        progress < 0.8f -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (resource.resourceType) {
                            ResourceType.WATER -> Icons.Outlined.WaterDrop
                            ResourceType.SEEDS -> Icons.Outlined.Grass
                            ResourceType.FERTILIZER -> Icons.Outlined.Eco
                            ResourceType.PESTICIDE -> Icons.Outlined.Science
                            ResourceType.LABOR -> Icons.Outlined.Person
                            ResourceType.ELECTRICITY -> Icons.Outlined.Bolt
                            ResourceType.FUEL -> Icons.Outlined.LocalGasStation
                        },
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = resource.resourceType.name.replace("_", " "),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${resource.used.toInt()} / ${resource.planned.toInt()} ${resource.unit}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${resource.cost.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% used",
                        fontSize = 11.sp,
                        color = progressColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = Color(0xFFF5F5F5)
            )
        }
    }
}

@Composable
fun CropComparisonCard(comparison: CropComparison) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comparison.cropName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "ROI: ${comparison.roi.toInt()}%",
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${(comparison.profit / 1000).toInt()}K",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "Profit",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(16.dp))
                
                ComparisonDetailRow("Water Usage", "${(comparison.waterUsage / 1000).toInt()}K L")
                Spacer(modifier = Modifier.height(8.dp))
                ComparisonDetailRow("Fertilizer", "${comparison.fertilizerUsage.toInt()} kg")
                Spacer(modifier = Modifier.height(8.dp))
                ComparisonDetailRow("Seed Cost", "₹${comparison.seedCost.toInt()}")
                Spacer(modifier = Modifier.height(8.dp))
                ComparisonDetailRow("Labor Cost", "₹${comparison.laborCost.toInt()}")
                Spacer(modifier = Modifier.height(8.dp))
                ComparisonDetailRow("Total Investment", "₹${comparison.totalCost.toInt()}", true)
                Spacer(modifier = Modifier.height(8.dp))
                ComparisonDetailRow("Expected Revenue", "₹${comparison.expectedRevenue.toInt()}", true)
            }
        }
    }
}

@Composable
fun ComparisonDetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color.Black,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SuggestionsTab(
    cropSuggestions: List<CropSuggestion>,
    landInfo: LandInfo
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Available Land: ${landInfo.availableArea} acres",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "Soil: ${landInfo.soilType} • pH: ${landInfo.phLevel}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Recommended Crops",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Based on your land, soil, and market conditions",
            fontSize = 13.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        cropSuggestions.sortedByDescending { it.suitabilityScore }.forEach { suggestion ->
            CropSuggestionCard(suggestion)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CropSuggestionCard(suggestion: CropSuggestion) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = suggestion.cropName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF4CAF50).copy(0.1f)
                        ) {
                            Text(
                                text = "${suggestion.suitabilityScore.toInt()}% Match",
                                fontSize = 11.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (suggestion.difficulty) {
                                "Easy" -> Color(0xFF4CAF50).copy(0.1f)
                                "Medium" -> Color(0xFFFF9800).copy(0.1f)
                                else -> Color(0xFFF44336).copy(0.1f)
                            }
                        ) {
                            Text(
                                text = suggestion.difficulty,
                                fontSize = 10.sp,
                                color = when (suggestion.difficulty) {
                                    "Easy" -> Color(0xFF4CAF50)
                                    "Medium" -> Color(0xFFFF9800)
                                    else -> Color(0xFFF44336)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF2196F3).copy(0.1f)
                        ) {
                            Text(
                                text = "${suggestion.growthDuration}d",
                                fontSize = 10.sp,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF9C27B0).copy(0.1f)
                        ) {
                            Text(
                                text = "${suggestion.marketDemand} Demand",
                                fontSize = 10.sp,
                                color = Color(0xFF9C27B0),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionMetric(
                    label = "Yield",
                    value = "${suggestion.expectedYield.toInt()} kg",
                    modifier = Modifier.weight(1f)
                )
                SuggestionMetric(
                    label = "Profit",
                    value = "₹${(suggestion.profitMargin / 1000).toInt()}K",
                    modifier = Modifier.weight(1f)
                )
                SuggestionMetric(
                    label = "Investment",
                    value = "₹${(suggestion.totalInvestment / 1000).toInt()}K",
                    modifier = Modifier.weight(1f)
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Resource Requirements",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                ResourceRequirementRow(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Water",
                    value = "${(suggestion.waterRequirement / 1000).toInt()}K L",
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResourceRequirementRow(
                    icon = Icons.Outlined.Eco,
                    label = "Fertilizer",
                    value = "${suggestion.fertilizerRequirement.toInt()} kg",
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResourceRequirementRow(
                    icon = Icons.Outlined.Grass,
                    label = "Seeds",
                    value = "₹${suggestion.seedCost.toInt()}",
                    color = Color(0xFFFF9800)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Start Growing ${suggestion.cropName}")
                }
            }
        }
    }
}

@Composable
fun SuggestionMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ResourceRequirementRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

private fun formatScheduleTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = timestamp - now
    val hours = diff / (1000 * 60 * 60)
    val days = hours / 24
    
    return when {
        days > 1 -> "in ${days}d"
        hours > 1 -> "in ${hours}h"
        else -> "Soon"
    }
}

private fun formatDateFarm(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
