package com.example.customlauncher

import android.content.Intent
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
    private lateinit var weatherIcon: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        timeText = findViewById(R.id.timeText)
        dateText = findViewById(R.id.dateText)
        weatherIcon = findViewById(R.id.weatherIcon)

        // Setup card clicks
        setupCardClicks()

        // Start clock update
        updateClockAndStatus()
    }

    private fun setupCardClicks() {
        findViewById<CardView>(R.id.cardApps).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<CardView>(R.id.cardAllApps).setOnClickListener {
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

        // SETTINGS - BUKA SETTINGS DEVICE
        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            try {
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

                    // Weather icon (static for now)
                    weatherIcon.text = "☀️"

                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onBackPressed() {
        // Disable back button di home screen
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}