package com.example.early_table_waiting.model

data class WaitingOwnerResponseDto(
    val waitingCount: Int,        // 대기 팀 수
    val waitingType: String,      // 웨이팅 타입 (예: "포장", "매장" 등)
    val waitingList: List<Any>,
    val waitingTime: Int
)

