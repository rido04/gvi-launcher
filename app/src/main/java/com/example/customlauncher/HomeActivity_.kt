package com.example.customlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var timeText: TextView
    private lateinit var dateText: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var wifiIcon: ImageView
    private lateinit var imageLoader: ImageLoader
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

        // Initialize Coil ImageLoader with SVG support
        imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        // Initialize views
        timeText = findViewById(R.id.timeText)
        dateText = findViewById(R.id.dateText)
        weatherIcon = findViewById(R.id.weatherIcon)
        wifiIcon = findViewById(R.id.wifiIcon)

        // Setup cards
        setupCardClicks()

        // Load WiFi icon after view is ready
        wifiIcon.post {
            loadWifiIcon()
        }

        // Clock update
        updateClockAndStatus()
    }

    private fun setupCardClicks() {
        // GVI App - CARI BERDASARKAN NAMA
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

        // Play Store
        findViewById<android.widget.FrameLayout>(R.id.cardGames).setOnClickListener {
            launchPlayStore()
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

        // File Manager - SEARCH BY NAME
        findViewById<android.widget.FrameLayout>(R.id.cardExtra).setOnClickListener {
            launchFileManager()
        }
    }

    private fun launchPlayStore() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.android.vending")
            if (intent != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store"))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchGVIApp() {
        try {
            val pm = packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            
            // Cari app yang namanya mengandung "gvi", "signage", atau "digital"
            for (app in apps) {
                val appName = app.loadLabel(pm).toString().lowercase()
                
                if (appName.contains("gvi") || 
                    appName.contains("signage") || 
                    appName.contains("digital smart")) {
                    
                    android.util.Log.d("HomeActivity", "Found GVI app: $appName (${app.activityInfo.packageName})")
                    
                    val launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                        return
                    }
                }
            }
            
            // Kalo gak ketemu, buka All Apps
            android.util.Log.w("HomeActivity", "GVI app not found, opening All Apps")
            startActivity(Intent(this, MainActivity::class.java))
            
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: buka All Apps
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun launchFileManager() {
        try {
            val pm = packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            
            // Cari app yang namanya mengandung "file", "manager", atau "files"
            for (app in apps) {
                val appName = app.loadLabel(pm).toString().lowercase()
                val packageName = app.activityInfo.packageName.lowercase()
                
                // Skip Gallery app (biar gak kebuka gallery lagi!)
                if (appName.contains("gallery") || packageName.contains("gallery")) {
                    continue
                }
                
                if (appName.contains("file") && appName.contains("manager") ||
                    appName.contains("filemanager") ||
                    appName.contains("files") && !appName.contains("google") ||
                    packageName.contains("filemanager") ||
                    packageName.contains("documentsui")) {
                    
                    android.util.Log.d("HomeActivity", "Found File Manager: $appName (${app.activityInfo.packageName})")
                    
                    val launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                        return
                    }
                }
            }
            
            // Fallback: Coba package name langsung
            val fileManagerPackages = listOf(
                "com.android.documentsui",
                "com.google.android.documentsui",
                "com.mi.android.globalFileexplorer"
            )
            
            for (pkg in fileManagerPackages) {
                try {
                    val launchIntent = pm.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        android.util.Log.d("HomeActivity", "Launching file manager: $pkg")
                        startActivity(launchIntent)
                        return
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            
            // Final fallback: Intent picker
            android.util.Log.w("HomeActivity", "No file manager found, showing chooser")
            val chooserIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startActivity(Intent.createChooser(chooserIntent, "Select File Manager"))
            
        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun loadWifiIcon() {
        val wifiUrl = "https://raw.githubusercontent.com/tabler/tabler-icons/master/icons/outline/wifi.svg"
        
        val request = ImageRequest.Builder(this)
            .data(wifiUrl)
            .target(
                onSuccess = { result ->
                    wifiIcon.setImageDrawable(result)
                    wifiIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                }
            )
            .build()
        
        imageLoader.enqueue(request)
    }

    private fun updateWeatherIcon() {
        try {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            
            val (iconUrl, color) = when {
                hour in 6..11 -> Pair(
                    "https://raw.githubusercontent.com/tabler/tabler-icons/master/icons/outline/sunrise.svg",
                    Color.parseColor("#FDB813")
                )
                hour in 12..17 -> Pair(
                    "https://raw.githubusercontent.com/tabler/tabler-icons/master/icons/outline/sun.svg",
                    Color.parseColor("#FDB813")
                )
                hour in 18..19 -> Pair(
                    "https://raw.githubusercontent.com/tabler/tabler-icons/master/icons/outline/sunset.svg",
                    Color.parseColor("#FF6B6B")
                )
                else -> Pair(
                    "https://raw.githubusercontent.com/tabler/tabler-icons/master/icons/outline/moon.svg",
                    Color.parseColor("#A8DADC")
                )
            }
            
            val request = ImageRequest.Builder(this)
                .data(iconUrl)
                .target(
                    onSuccess = { result ->
                        weatherIcon.setImageDrawable(result)
                        weatherIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                )
                .build()
            
            imageLoader.enqueue(request)
            
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

    override fun onBackPressed() {
        // Disable back button pada launcher
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}