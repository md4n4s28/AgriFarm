package com.agrifarm.app.data.model

data class CropSessionDetail(
    val id: String = "",
    val cropName: String = "",
    val cropType: String = "",
    val variety: String = "",
    val landArea: Float = 0f,
    val seedingDate: Long = 0L,
    val expectedHarvestDate: Long = 0L,
    val currentStage: CropStage = CropStage.SEEDING,
    val daysElapsed: Int = 0,
    val daysRemaining: Int = 0,
    val healthStatus: String = "Good",
    val expectedYield: Float = 0f,
    val currentYield: Float = 0f
)

enum class CropStage {
    SEEDING, GERMINATION, VEGETATIVE, FLOWERING, MATURITY, HARVESTING
}

data class Schedule(
    val id: String = "",
    val type: ScheduleType,
    val title: String = "",
    val description: String = "",
    val scheduledTime: Long = 0L,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurringDays: List<Int> = emptyList()
)

enum class ScheduleType {
    WATERING, FERTILIZER, PESTICIDE, INSPECTION, HARVESTING, OTHER
}

data class PumpControl(
    val isOn: Boolean = false,
    val mode: PumpMode = PumpMode.MANUAL,
    val scheduledStartTime: String = "",
    val scheduledEndTime: String = "",
    val duration: Int = 0,
    val waterUsed: Float = 0f,
    val lastRunTime: Long = 0L
)

enum class PumpMode {
    MANUAL, SCHEDULED, AUTO
}

data class WeatherAlert(
    val id: String = "",
    val type: AlertType,
    val severity: AlertSeverity,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val actionRequired: String = ""
)

enum class AlertType {
    MONSOON, HEAVY_RAIN, DROUGHT, FROST, HEATWAVE, STORM, GENERAL
}

enum class AlertSeverity {
    INFO, LOW, MEDIUM, HIGH, CRITICAL
}

data class LandInfo(
    val totalArea: Float = 0f,
    val usedArea: Float = 0f,
    val availableArea: Float = 0f,
    val soilType: String = "",
    val phLevel: Float = 0f,
    val location: String = "",
    val coordinates: Pair<Double, Double> = Pair(0.0, 0.0)
)

data class CropSuggestion(
    val cropName: String = "",
    val suitabilityScore: Float = 0f,
    val expectedYield: Float = 0f,
    val waterRequirement: Float = 0f,
    val fertilizerRequirement: Float = 0f,
    val seedCost: Float = 0f,
    val totalInvestment: Float = 0f,
    val expectedRevenue: Float = 0f,
    val profitMargin: Float = 0f,
    val growthDuration: Int = 0,
    val difficulty: String = "",
    val marketDemand: String = ""
)

data class ResourceUsage(
    val resourceType: ResourceType,
    val used: Float = 0f,
    val planned: Float = 0f,
    val remaining: Float = 0f,
    val cost: Float = 0f,
    val unit: String = ""
)

enum class ResourceType {
    WATER, SEEDS, FERTILIZER, PESTICIDE, LABOR, ELECTRICITY, FUEL
}

data class CropComparison(
    val cropName: String = "",
    val waterUsage: Float = 0f,
    val fertilizerUsage: Float = 0f,
    val seedCost: Float = 0f,
    val laborCost: Float = 0f,
    val totalCost: Float = 0f,
    val expectedRevenue: Float = 0f,
    val profit: Float = 0f,
    val roi: Float = 0f
)
