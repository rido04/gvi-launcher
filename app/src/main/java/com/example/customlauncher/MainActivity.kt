package com.example.customlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appAdapter: AppAdapter
    private lateinit var clockText: TextView
    private lateinit var wifiStatus: TextView
    private val appList = mutableListOf<AppInfo>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)

            recyclerView = findViewById(R.id.recyclerView)
            clockText = findViewById(R.id.clockText)
            wifiStatus = findViewById(R.id.wifiStatus)

            recyclerView.layoutManager = GridLayoutManager(this, 5)

            loadInstalledApps()

            appAdapter = AppAdapter(appList) { appInfo ->
                launchApp(appInfo.packageName)
            }
            recyclerView.adapter = appAdapter

            // Start clock update (tanpa WiFi status dulu)
            updateClock()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateClock() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    clockText.text = sdf.format(Date())
                    
                    // Static WiFi icon dulu
                    wifiStatus.text = "📶"
                    
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun loadInstalledApps() {
        try {
            val pm = packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

            for (app in apps) {
                if (app.activityInfo.packageName == packageName) continue

                val appInfo = AppInfo(
                    label = app.loadLabel(pm).toString(),
                    packageName = app.activityInfo.packageName,
                    icon = app.loadIcon(pm)
                )
                appList.add(appInfo)
            }

            appList.sortBy { it.label.lowercase() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchApp(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        // Disable back button
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}