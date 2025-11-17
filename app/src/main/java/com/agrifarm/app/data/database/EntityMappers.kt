package com.agrifarm.app.data.database

import com.agrifarm.app.data.model.*

// Crop Session Mappers
fun CropSessionEntity.toDomain(): CropSessionDetail {
    val now = System.currentTimeMillis()
    val daysElapsed = ((now - seedingDate) / (24 * 60 * 60 * 1000)).toInt()
    val daysRemaining = ((expectedHarvestDate - now) / (24 * 60 * 60 * 1000)).toInt()
    
    return CropSessionDetail(
        id = id ?: "",
        cropName = cropName,
        cropType = cropType,
        variety = variety ?: "",
        landArea = landArea,
        seedingDate = seedingDate,
        expectedHarvestDate = expectedHarvestDate,
        currentStage = CropStage.valueOf(currentStage),
        daysElapsed = daysElapsed,
        daysRemaining = daysRemaining,
        healthStatus = healthStatus,
        expectedYield = expectedYield,
        currentYield = currentYield
    )
}

fun CropSessionDetail.toEntity(userId: String) = CropSessionEntity(
    id = if (id.isEmpty()) null else id,
    userId = userId,
    cropName = cropName,
    cropType = cropType,
    variety = variety,
    landArea = landArea,
    seedingDate = seedingDate,
    expectedHarvestDate = expectedHarvestDate,
    currentStage = currentStage.name,
    healthStatus = healthStatus,
    expectedYield = expectedYield,
    currentYield = currentYield
)

// Schedule Mappers
fun ScheduleEntity.toDomain() = Schedule(
    id = id ?: "",
    type = ScheduleType.valueOf(type),
    title = title,
    description = description ?: "",
    scheduledTime = scheduledTime,
    isCompleted = isCompleted,
    isRecurring = isRecurring,
    recurringDays = recurringDays
)

fun Schedule.toEntity(userId: String) = ScheduleEntity(
    id = if (id.isEmpty()) null else id,
    userId = userId,
    type = type.name,
    title = title,
    description = description,
    scheduledTime = scheduledTime,
    isCompleted = isCompleted,
    isRecurring = isRecurring,
    recurringDays = recurringDays
)

// Pump Control Mappers
fun PumpControlEntity.toDomain() = PumpControl(
    isOn = isOn,
    mode = PumpMode.valueOf(mode),
    scheduledStartTime = scheduledStartTime ?: "",
    scheduledEndTime = scheduledEndTime ?: "",
    duration = duration,
    waterUsed = waterUsed,
    lastRunTime = lastRunTime ?: 0L
)

fun PumpControl.toEntity(userId: String) = PumpControlEntity(
    userId = userId,
    isOn = isOn,
    mode = mode.name,
    scheduledStartTime = scheduledStartTime,
    scheduledEndTime = scheduledEndTime,
    duration = duration,
    waterUsed = waterUsed,
    lastRunTime = lastRunTime
)

// Weather Alert Mappers
fun WeatherAlertEntity.toDomain() = WeatherAlert(
    id = id ?: "",
    type = AlertType.valueOf(type),
    severity = AlertSeverity.valueOf(severity),
    title = title,
    message = message,
    timestamp = timestamp,
    actionRequired = actionRequired ?: ""
)

// Land Info Mappers
fun LandInfoEntity.toDomain() = LandInfo(
    totalArea = totalArea,
    usedArea = usedArea,
    availableArea = availableArea ?: 0f,
    soilType = soilType ?: "",
    phLevel = phLevel ?: 0f,
    location = location ?: "",
    coordinates = Pair(latitude?.toDouble() ?: 0.0, longitude?.toDouble() ?: 0.0)
)

fun LandInfo.toEntity(userId: String) = LandInfoEntity(
    userId = userId,
    totalArea = totalArea,
    usedArea = usedArea,
    availableArea = availableArea,
    soilType = soilType,
    phLevel = phLevel,
    location = location,
    latitude = coordinates.first.toFloat(),
    longitude = coordinates.second.toFloat()
)

// Crop Suggestion Mappers
fun CropSuggestionEntity.toDomain() = CropSuggestion(
    cropName = cropName,
    suitabilityScore = suitabilityScore,
    expectedYield = expectedYield ?: 0f,
    waterRequirement = waterRequirement ?: 0f,
    fertilizerRequirement = fertilizerRequirement ?: 0f,
    seedCost = seedCost ?: 0f,
    totalInvestment = totalInvestment ?: 0f,
    expectedRevenue = expectedRevenue ?: 0f,
    profitMargin = profitMargin ?: 0f,
    growthDuration = growthDuration ?: 0,
    difficulty = difficulty ?: "",
    marketDemand = marketDemand ?: ""
)

// Resource Usage Mappers
fun ResourceUsageEntity.toDomain() = ResourceUsage(
    resourceType = ResourceType.valueOf(resourceType),
    used = used,
    planned = planned,
    remaining = remaining,
    cost = cost,
    unit = unit
)

// Crop Comparison Mappers
fun CropComparisonEntity.toDomain() = CropComparison(
    cropName = cropName,
    waterUsage = waterUsage ?: 0f,
    fertilizerUsage = fertilizerUsage ?: 0f,
    seedCost = seedCost ?: 0f,
    laborCost = laborCost ?: 0f,
    totalCost = totalCost ?: 0f,
    expectedRevenue = expectedRevenue ?: 0f,
    profit = profit ?: 0f,
    roi = roi ?: 0f
)
