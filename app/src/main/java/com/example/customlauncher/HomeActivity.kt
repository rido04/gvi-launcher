package com.example.customlauncher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var timeText: TextView
    private lateinit var dateText: TextView
    private lateinit var weatherIcon: TextView
    private lateinit var wifiIcon: TextView
    private val handler = Handler(Looper.getMainLooper())
    
    // Cache Intent
    private val settingsIntent by lazy { Intent(android.provider.Settings.ACTION_SETTINGS) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // DISABLE TRANSITIONS - KUNCI PERFORMA!
        window.enterTransition = null
        window.exitTransition = null
        window.reenterTransition = null
        window.returnTransition = null
        
        setContentView(R.layout.activity_home_optimized)

        // Initialize views
        timeText = findViewById(R.id.timeText)
        dateText = findViewById(R.id.dateText)
        weatherIcon = findViewById(R.id.weatherIcon)
        wifiIcon = findViewById(R.id.wifiIcon)

        // Setup cards
        setupCardClicks()

        // Clock update
        updateClockAndStatus()
    }

    private fun setupCardClicks() {
        // GVI App
        findViewById<android.widget.FrameLayout>(R.id.cardApps).setOnClickListener {
            launchGVIApp()
        }

        // YouTube
        findViewById<android.widget.FrameLayout>(R.id.cardMusic).setOnClickListener {
            launchApp("com.google.android.youtube.tv", "https://www.youtube.com")
        }

        // Chrome
        findViewById<android.widget.FrameLayout>(R.id.cardVideo).setOnClickListener {
            launchApp("com.android.chrome", "https://www.google.com")
        }

        // Games
        findViewById<android.widget.FrameLayout>(R.id.cardGames).setOnClickListener {
            // TODO: Launch games
        }

        // Settings
        findViewById<android.widget.FrameLayout>(R.id.cardSettings).setOnClickListener {
            try {
                startActivity(settingsIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // All Apps
        findViewById<android.widget.FrameLayout>(R.id.cardAllApps).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // File Manager
        findViewById<android.widget.FrameLayout>(R.id.cardExtra).setOnClickListener {
            launchFileManager()
        }
    }

    private fun launchApp(packageName: String, fallbackUrl: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(fallbackUrl))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchFileManager() {
        try {
            val fileManagers = listOf(
                "com.android.documentsui",
                "com.google.android.documentsui",
                "com.mi.android.globalFileexplorer"
            )
            
            for (pkg in fileManagers) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage(pkg)
                    if (intent != null) {
                        startActivity(intent)
                        return
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            
            // Fallback
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivity(Intent.createChooser(intent, "Select File Manager"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchGVIApp() {
        try {
            val packageNames = listOf(
                "com.gvi.smartsignage",
                "com.gvi.digitalsignage",
                "com.globalvision.intermedia"
            )

            for (packageName in packageNames) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        startActivity(intent)
                        return
                    }
                } catch (e: Exception) {
                    continue
                }
            }

            // Fallback ke All Apps
            startActivity(Intent(this, MainActivity::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateClockAndStatus() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    timeText.text = timeFormat.format(Date())

                    val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
                    dateText.text = dateFormat.format(Date())

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
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            weatherIcon.text = when {
                hour in 6..11 -> "🌅"
                hour in 12..17 -> "☀️"
                hour in 18..19 -> "🌇"
                else -> "🌙"
            }
        } catch (e: Exception) {
            weatherIcon.text = "☀️"
        }
    }

    override fun onBackPressed() {
        // Disable back button pada launcher
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}