package com.example.early_table_waiting.model

data class WaitingNumberResponseDto(
    val waitingId: Long,          // 웨이팅 ID
    val waitingNumber: Long,      // 웨이팅 번호
    val waitingTime: String       // 예상 대기 시간
)