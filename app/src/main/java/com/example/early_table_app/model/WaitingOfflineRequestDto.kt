package com.example.early_table_waiting.model


data class WaitingOfflineRequestDto(
    val phoneNumber: String,
    val personnelCount: Int,
    val waitingType: String
)