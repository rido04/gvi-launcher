package com.example.customlauncher

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var timeText: TextView
    private lateinit var dateText: TextView
    private lateinit var wifiIcon: TextView
    private lateinit var weatherIcon: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        timeText = findViewById(R.id.timeText)
        dateText = findViewById(R.id.dateText)
        wifiIcon = findViewById(R.id.wifiIcon)
        weatherIcon = findViewById(R.id.weatherIcon)

        // Setup card clicks
        setupCardClicks()

        // Start clock update
        updateClockAndStatus()
    }

    private fun setupCardClicks() {
        findViewById<CardView>(R.id.cardApps).setOnClickListener {
            // Pindah ke MainActivity (list apps)
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<CardView>(R.id.cardAllApps).setOnClickListener {
            // Pindah ke MainActivity (list apps)
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<CardView>(R.id.cardMusic).setOnClickListener {
            // TODO: Buka Music category
        }

        findViewById<CardView>(R.id.cardVideo).setOnClickListener {
            // TODO: Buka Video category
        }

        findViewById<CardView>(R.id.cardGames).setOnClickListener {
            // TODO: Buka Games category
        }

        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            // TODO: Buka Settings
        }
    }

    private fun updateClockAndStatus() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    // Update time
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timeText.text = timeFormat.format(Date())

                    // Update date
                    val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
                    dateText.text = dateFormat.format(Date())

                    // Update WiFi status
                    updateWifiStatus()

                    // Weather icon (static for now)
                    weatherIcon.text = "☀️" // Bisa diganti: ☁️ 🌧️ ⛈️

                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateWifiStatus() {
        try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            wifiIcon.text = when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "📶"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "📱"
                else -> "📵"
            }
        } catch (e: Exception) {
            wifiIcon.text = "📶"
        }
    }

    override fun onBackPressed() {
        // Disable back button di home screen
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}