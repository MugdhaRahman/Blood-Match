package com.mrapps.bloodmatch.data.model

data class BloodRequest(
    val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val bloodGroup: String = "",
    val units: Int = 1,
    val hospital: String = "",
    val city: String = "",
    val urgency: String = "Normal",
    val note: String = "",
    val status: String = "OPEN",
    val createdAt: Long = System.currentTimeMillis()
)
