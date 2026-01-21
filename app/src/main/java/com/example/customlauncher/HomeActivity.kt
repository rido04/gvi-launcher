package com.example.customlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
        // GVI SMART SIGNAGE
        findViewById<CardView>(R.id.cardApps).setOnClickListener {
            launchGVIApp()
        }

        // YOUTUBE
        findViewById<CardView>(R.id.cardMusic).setOnClickListener {
            launchYouTube()
        }

        // CHROME
        findViewById<CardView>(R.id.cardVideo).setOnClickListener {
            launchChrome()
        }

        // GAMES
        findViewById<CardView>(R.id.cardGames).setOnClickListener {
            // TODO: Buka Games category atau app tertentu
        }

        // SETTINGS
        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            try {
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // ALL APPS
        findViewById<CardView>(R.id.cardAllApps).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // WIFI
        findViewById<CardView>(R.id.cardExtra).setOnClickListener {
            try {
                val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGVIApp() {
        try {
            val packageNames = listOf(
                "com.gvi.smartsignage",
                "com.gvi.digitalsignage",
                "com.globalvision.intermedia",
                "com.example.gvi"
            )

            var launched = false
            for (packageName in packageNames) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        startActivity(intent)
                        launched = true
                        break
                    }
                } catch (e: Exception) {
                    continue
                }
            }

            if (!launched) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchYouTube() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
            if (intent != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com"))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchChrome() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.android.chrome")
            if (intent != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com"))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateClockAndStatus() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    // Update time (24-hour format)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timeText.text = timeFormat.format(Date())

                    // Update date (FULL NAME: Tuesday, January 20)
                    val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                    dateText.text = dateFormat.format(Date())

                    // Update weather icon (REAL dari waktu/kondisi)
                    updateWeatherIcon()

                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateWeatherIcon() {
        try {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            
            // Simple logic based on time of day
            weatherIcon.text = when {
                hour in 6..11 -> "🌅" // Pagi (Morning)
                hour in 12..17 -> "☀️" // Siang (Afternoon)
                hour in 18..19 -> "🌇" // Sore (Evening)
                else -> "🌙" // Malam (Night)
            }
            
            // Alternative: bisa juga random/seasonal
            // val month = calendar.get(Calendar.MONTH)
            // weatherIcon.text = when (month) {
            //     Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> "❄️" // Winter
            //     Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> "🌸" // Spring
            //     Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> "☀️" // Summer
            //     else -> "🍂" // Fall
            // }
            
        } catch (e: Exception) {
            weatherIcon.text = "☀️" // Fallback
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