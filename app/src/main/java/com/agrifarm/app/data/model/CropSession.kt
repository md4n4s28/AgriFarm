package com.agrifarm.app.data.model

data class CropSession(
    val id: String = "",
    val cropName: String = "",
    val cropType: String = "",
    val landArea: Float = 0f,
    val seedingDate: Long = 0L,
    val expectedHarvestDate: Long = 0L,
    val currentStage: CropStage = CropStage.SEEDING,
    val daysRemaining: Int = 0
)

data class WateringSchedule(
    val frequency: String = "Daily",
    val duration: Int = 30,
    val nextWatering: Long = 0L,
    val autoMode: Boolean = false
)

data class PumpStatus(
    val isOn: Boolean = false,
    val mode: String = "Manual",
    val scheduledTimes: List<String> = emptyList()
)
