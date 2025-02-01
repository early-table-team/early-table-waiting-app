package com.example.early_table_waiting

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.early_table_waiting.model.WaitingNumberResponseDto
import com.example.early_table_waiting.model.WaitingOfflineRequestDto
import com.example.early_table_waiting.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DineInWaitingActivity : AppCompatActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var personCountText: TextView
    private var personCount = 3
    private var waitingType : String = "DINE_IN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dine_in_waiting)

        // 가게 이름 텍스트
        val storeNameText: TextView = findViewById(R.id.storeNameText)
        val selectedStore = StoreDataHolder.selectedStore
        if (selectedStore != null) {
            storeNameText.text = selectedStore.name
        } else {
            storeNameText.text = "가게 이름 없음"
        }

        // 전화번호 입력 EditText
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        phoneNumberEditText.hint = "010-0000-0000"

        // 인원수 표시
        personCountText = findViewById(R.id.personCountText)
        personCountText.text = personCount.toString()

        // 인원수 조정 버튼들
        val btnDecrement: Button = findViewById(R.id.btnDecrement)
        val btnIncrement: Button = findViewById(R.id.btnIncrement)

        btnDecrement.setOnClickListener {
            if (personCount > 1) {
                personCount--
                personCountText.text = personCount.toString()
            }
        }

        btnIncrement.setOnClickListener {
            personCount++
            personCountText.text = personCount.toString()
        }

        // 예약 버튼 클릭
        val btnReserve: Button = findViewById(R.id.btnReserve)
        btnReserve.setOnClickListener {
            registerWaiting()
        }

        // 전화번호 버튼 처리
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is Button) {
                child.setOnClickListener {
                    val currentText = phoneNumberEditText.text.toString()
                    val buttonText = (it as Button).text.toString()
                    if (buttonText == "C") {
                        if (currentText.isNotEmpty()) {
                            phoneNumberEditText.setText(currentText.substring(0, currentText.length - 1))
                        }
                    } else {
                        // 전화번호 형식에 맞게 텍스트 입력 처리
                        if (currentText.length < 13) {
                            if (currentText.length == 3 || currentText.length == 8) {
                                phoneNumberEditText.append("-")
                            }
                            phoneNumberEditText.append(buttonText)
                        }
                    }
                }
            }
        }
    }
    private fun registerWaiting() {
        val phoneNumber = phoneNumberEditText.text.toString()
        val selectedStore = StoreDataHolder.selectedStore

        if (selectedStore == null) {
            Toast.makeText(this, "선택된 가게가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val waitingRequest = WaitingOfflineRequestDto(
            phoneNumber = phoneNumber,
            personnelCount = personCount,
            waitingType = waitingType
        )

        val apiService = ApiClient.apiService
        Log.d("DineInWaitingActivity", "Sending API request: $waitingRequest")

        apiService.createWaitingOffline(waitingRequest, selectedStore.id).enqueue(object : Callback<WaitingNumberResponseDto> {
            override fun onResponse(call: Call<WaitingNumberResponseDto>, response: Response<WaitingNumberResponseDto>) {
                if (response.isSuccessful) {
                    // 응답 본문에서 WaitingNumberResponseDto 추출
                    val waitingResponse = response.body()
                    if (waitingResponse != null) {
                        val waitingId = waitingResponse.waitingId

                        // SharedPreferences에 저장
                        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("DINE_IN_waiting_id", waitingId.toString())
                        editor.apply()

                        // 저장된 값 확인 (선택적)
                        val savedWaitingId = sharedPreferences.getString("DINE_IN_waiting_id", null)
                        Log.d("WaitingIdCheck", "Saved waitingId in SharedPreferences: $savedWaitingId")
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 2000)
                    } else {
                        Log.d("WaitingIdCheck", "WaitingNumberResponseDto is null")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("DineInWaitingActivity", "API response failed: ${response.code()} $errorMsg")
                    runOnUiThread {
                        Toast.makeText(this@DineInWaitingActivity, "예약 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<WaitingNumberResponseDto>, t: Throwable) {
                Log.e("DineInWaitingActivity", "API call failed: ${t.message}")
                Toast.makeText(this@DineInWaitingActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
