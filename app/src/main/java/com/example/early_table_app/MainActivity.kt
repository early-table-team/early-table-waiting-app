package com.example.early_table_waiting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.early_table_waiting.model.LoginRequest
import com.example.early_table_waiting.model.LoginResponse
import com.example.early_table_waiting.network.ApiClient
import com.example.early_table_waiting.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity)  // 로그인 화면 레이아웃

        val loginButton: Button = findViewById(R.id.loginButton)
        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)

        loginButton.setOnClickListener {
            // 로그 확인
            Log.d("MainActivity", "Login button clicked!")
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loginUser(username: String, password: String) {
        val apiService = ApiClient.apiService

        val loginRequest = LoginRequest(username, password)
        val call = apiService.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("MainActivity", "API response received")
                if (response.isSuccessful) {
                    val token = response.headers()["Authorization"]
                    if (token != null) {
                        Toast.makeText(this@MainActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                        // 토큰 저장
                        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", token)
                        editor.apply()

                        // 다음 화면으로 전환
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                    } else {
                        Toast.makeText(this@MainActivity, "로그인 실패: 서버에서 토큰을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("MainActivity", "API failed with code: ${response.code()}")
                    Toast.makeText(this@MainActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
                Toast.makeText(this@MainActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
