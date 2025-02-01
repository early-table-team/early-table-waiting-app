package com.example.early_table_waiting.model

enum class WaitingType {
    DINE_IN, TO_GO
}

data class WaitingSimpleOwnerRequestDto(
    val waitingType: WaitingType
)