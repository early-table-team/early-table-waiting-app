package com.example.early_table_waiting

import com.example.early_table_waiting.model.Store
import com.example.early_table_waiting.model.StoreListResponseDto

object StoreDataHolder {
    var storeList: List<StoreListResponseDto> = listOf()
    var selectedStore: Store? = null
}