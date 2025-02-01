package com.example.early_table_waiting

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.early_table_waiting.model.WaitingOwnerResponseDto
import com.example.early_table_waiting.model.WaitingSimpleOwnerRequestDto
import com.example.early_table_waiting.model.WaitingType
import com.example.early_table_waiting.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaitingSelectionActivity : AppCompatActivity() {

    private lateinit var teamsWaitingTextDineIn: TextView
    private lateinit var estimatedWaitTimeDineIn: TextView
    private lateinit var teamsWaitingTextTakeout: TextView
    private lateinit var estimatedWaitTimeTakeout: TextView

    private val handler = Handler() // 핸들러
    private val updateRunnable: Runnable = object : Runnable {
        override fun run() {
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val storeId = sharedPreferences.getLong("store_id", -1) // store_id가 없으면 -1 반환

            if (storeId != -1L) {
                // 매장 웨이팅 상태 조회
                checkWaitingStatus(storeId, WaitingType.DINE_IN, teamsWaitingTextDineIn, estimatedWaitTimeDineIn)
                checkWaitingStatus(storeId, WaitingType.TO_GO, teamsWaitingTextTakeout, estimatedWaitTimeTakeout)
            } else {
                Log.e("WaitingSelectionActivity", "Store ID not found in SharedPreferences")
            }

            // 3분 후 다시 실행
            handler.postDelayed(this, 3 * 60 * 1000) // 3분(180초)마다 호출
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_selection)

        val btnTakeout = findViewById<Button>(R.id.btnTakeout)
        val btnDineIn = findViewById<Button>(R.id.btnDineIn)
        val storeNameTextView = findViewById<TextView>(R.id.storeNameTextView)

        teamsWaitingTextDineIn = findViewById(R.id.teamsWaitingTextDineIn)
        estimatedWaitTimeDineIn = findViewById(R.id.estimatedWaitTimeDineIn)
        teamsWaitingTextTakeout = findViewById(R.id.teamsWaitingTextTakeout)
        estimatedWaitTimeTakeout = findViewById(R.id.estimatedWaitTimeTakeout)

        val selectedStore = StoreDataHolder.selectedStore
        storeNameTextView.text = selectedStore?.name ?: "가게 이름 없음"

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val storeId = sharedPreferences.getLong("store_id", -1) // store_id가 없으면 -1 반환

        if (storeId != -1L) {
            // 매장 웨이팅 상태 조회
            checkWaitingStatus(storeId, WaitingType.DINE_IN, teamsWaitingTextDineIn, estimatedWaitTimeDineIn)
            checkWaitingStatus(storeId, WaitingType.TO_GO, teamsWaitingTextTakeout, estimatedWaitTimeTakeout)
        } else {
            Log.e("WaitingSelectionActivity", "Store ID not found in SharedPreferences")
        }

        // 매장 웨이팅 선택
        btnDineIn.setOnClickListener {
            val intent = Intent(this, DineInWaitingActivity::class.java)
            startActivity(intent)
        }

        btnTakeout.setOnClickListener{
            val intent = Intent(this, TakeoutWaitingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 화면에 표시되는 데이터를 3분마다 갱신하기 위해 Runnable을 실행
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        // 화면이 비활성화 되면 주기적 갱신을 중지
        handler.removeCallbacks(updateRunnable)
    }

    private fun checkWaitingStatus(
        storeId: Long,
        waitingType: WaitingType,
        teamsWaitingText: TextView,
        estimatedWaitTimeText: TextView
    ) {
        val apiService = ApiClient.apiService

        // WaitingSimpleOwnerRequestDto 생성
        val requestDto = WaitingSimpleOwnerRequestDto(waitingType)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)
        val bearerToken = "Bearer $authToken"

        if (authToken.isNullOrEmpty()) {
            teamsWaitingText.text = "인증 정보 없음"
            estimatedWaitTimeText.text = ""
            return
        }

        // API 호출
        apiService.getOwnerNowWaitingList(bearerToken, storeId, waitingType).enqueue(object : Callback<WaitingOwnerResponseDto> {
            override fun onResponse(
                call: Call<WaitingOwnerResponseDto>,
                response: Response<WaitingOwnerResponseDto>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val waitingInfo = response.body()!!

                    Log.d("WaitingInfo", "Received DTO: $waitingInfo")

                    // UI 업데이트는 메인 스레드에서 이루어져야 함
                    runOnUiThread {
                        teamsWaitingText.text = "현재 대기중인 팀 수: ${waitingInfo.waitingCount}팀"
                        val waitTime = if (waitingInfo.waitingTime == 0) 10 else waitingInfo.waitingTime
                        estimatedWaitTimeText.text = "예상 대기시간: ${waitTime}분"
                    }
                } else {
                    Log.d("WaitingInfo", "정보를 불러오지 못했습니다.")
                    runOnUiThread {
                        teamsWaitingText.text = "정보를 불러오지 못했습니다."
                        estimatedWaitTimeText.text = ""
                    }
                }
            }

            override fun onFailure(call: Call<WaitingOwnerResponseDto>, t: Throwable) {
                Log.d("WaitingInfo", "네트워크 오류: ${t.message}")
                runOnUiThread {
                    teamsWaitingText.text = "네트워크 오류"
                    estimatedWaitTimeText.text = ""
                }
            }
        })
    }
}
