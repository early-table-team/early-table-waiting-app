package com.example.early_table_waiting.model

import java.time.LocalDateTime

data class StoreListResponseDto(
    val storeId: Long,
    val storeName: String,
    val storeTel: String?,
    val storeContent: String?,
    val storeAddress: String?,
    val storeCategory: String?,
    val presentMenu: String?,
    val ownerName: String?,
    val createdAt: String?,  // String으로 변경
    val modifiedAt: String?, // String으로 변경
    val storeStatus: String?,
    val storeImageUrl: String?
)