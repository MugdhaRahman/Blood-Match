package com.mrapps.bloodmatch.data.model

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val availableToDonate: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
