package com.example.early_table_waiting.network

import com.example.early_table_waiting.model.LoginRequest
import com.example.early_table_waiting.model.LoginResponse
import com.example.early_table_waiting.model.Store
import com.example.early_table_waiting.model.StoreListResponseDto
import com.example.early_table_waiting.model.WaitingNumberResponseDto
import com.example.early_table_waiting.model.WaitingOfflineRequestDto
import com.example.early_table_waiting.model.WaitingOwnerResponseDto
import com.example.early_table_waiting.model.WaitingSimpleOwnerRequestDto
import com.example.early_table_waiting.model.WaitingType
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/users/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/stores/waiting/able") // 서버에서 가게 목록을 가져오는 엔드포인트
    fun getStores(@Header("Authorization") authToken: String): Call<List<StoreListResponseDto>>

    @POST("/stores/{storeId}/waiting/offline")
    fun createWaitingOffline(
        @Body request: WaitingOfflineRequestDto,
        @Path("storeId") storeId: Long
    ): Call<WaitingNumberResponseDto>

    @GET("/stores/{storeId}/waiting/now")
    fun getOwnerNowWaitingList(
        @Header("Authorization") authToken: String,
        @Path("storeId") storeId: Long,
        @Query("waitingType") waitingType: WaitingType
    ): Call<WaitingOwnerResponseDto>


}
