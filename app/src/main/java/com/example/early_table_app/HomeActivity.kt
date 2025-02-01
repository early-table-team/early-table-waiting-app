package com.example.early_table_waiting

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.early_table_waiting.adapter.StoreAdapter
import com.example.early_table_waiting.model.Store
import com.example.early_table_waiting.model.StoreListResponseDto
import com.example.early_table_waiting.network.ApiClient
import com.example.early_table_waiting.ui.StoreDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), StoreDialogFragment.StoreDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var storeList: MutableList<Store>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.storeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // API 호출
        loadStoresFromApi()
    }

    private fun loadStoresFromApi() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            Log.d("HomeActivity", "Token found: $token")
            val apiService = ApiClient.apiService
            val call = apiService.getStores("Bearer $token")
            Log.d("HomeActivity", "API call initiated")

            call.enqueue(object : Callback<List<StoreListResponseDto>> {
                override fun onResponse(
                    call: Call<List<StoreListResponseDto>>,
                    response: Response<List<StoreListResponseDto>>
                ) {
                    Log.d("HomeActivity", "API response received")
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("HomeActivity", "Response successful, parsing data")
                        // 서버에서 받은 데이터를 변환하여 Store 객체로 변환
                        storeList = response.body()!!.map {
                            Store(
                                id = it.storeId,
                                name = it.storeName,
                                address = it.storeAddress ?: "주소 없음",
                                imageUrl = it.storeImageUrl ?: "이미지 없음"
                            )
                        }.toMutableList()

                        // 어댑터에 데이터 설정
                        storeAdapter = StoreAdapter(storeList) { store ->
                            val dialog = StoreDialogFragment.newInstance(store)
                            dialog.show(supportFragmentManager, "StoreDialogFragment")
                        }
                        recyclerView.adapter = storeAdapter
                        Log.d("HomeActivity", "Adapter set with store data")
                    } else {
                        Log.e("HomeActivity", "Failed to load store list: ${response.code()}")
                        Toast.makeText(this@HomeActivity, "가게 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<StoreListResponseDto>>, t: Throwable) {
                    Log.e("HomeActivity", "API call failed: ${t.message}")
                    Toast.makeText(this@HomeActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.e("HomeActivity", "No token found, login required")
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //확인을 눌렀을때 호출되는 메서드
    override fun onConfirm() {
        Toast.makeText(this, "웨이팅을 설정합니다.", Toast.LENGTH_SHORT).show()
    }
}