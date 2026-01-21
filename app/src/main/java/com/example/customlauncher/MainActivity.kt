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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appAdapter: AppAdapter
    private lateinit var clockText: TextView
    private lateinit var wifiIcon: ImageView  // GANTI dari wifiStatus: TextView
    private lateinit var imageLoader: ImageLoader  // TAMBAH ini
    private val appList = mutableListOf<AppInfo>()
    private val handler = Handler(Looper.getMainLooper())
    
    // Coroutine scope untuk background loading
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)

            // Initialize Coil ImageLoader with SVG support
            imageLoader = ImageLoader.Builder(this)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            clockText = findViewById(R.id.clockText)
            wifiIcon = findViewById(R.id.wifiIcon)  // GANTI dari wifiStatus
            recyclerView = findViewById(R.id.recyclerView)

            // Setup RecyclerView dengan optimasi
            setupRecyclerView()

            // Load apps di background thread
            loadInstalledAppsAsync()

            // Load WiFi icon after view is ready
            wifiIcon.post {
                loadWifiIcon()
            }

            // Start clock update
            updateClock()
            
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
    
    private fun setupRecyclerView() {
        // GridLayoutManager dengan optimasi
        val layoutManager = GridLayoutManager(this, 5).apply {
            // Recycle views yang scroll out of bounds
            recycleChildrenOnDetach = true
        }
        recyclerView.layoutManager = layoutManager
        
        // Optimasi RecyclerView
        recyclerView.apply {
            setHasFixedSize(true)  // Ukuran item fixed, performa lebih baik
            setItemViewCacheSize(20)  // Cache 20 items
            isDrawingCacheEnabled = true
            drawingCacheQuality = android.view.View.DRAWING_CACHE_QUALITY_HIGH
        }
        
        // Initialize adapter kosong dulu
        appAdapter = AppAdapter(appList) { appInfo ->
            launchApp(appInfo.packageName)
        }
        recyclerView.adapter = appAdapter
    }

    private fun updateClock() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    clockText.text = sdf.format(Date())
                    
                    // WiFi icon sudah di-load dari loadWifiIcon(), tidak perlu update lagi
                    
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun loadInstalledAppsAsync() {
        // Load apps di background thread agar tidak freeze UI
        mainScope.launch(Dispatchers.IO) {
            try {
                val pm = packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                val tempList = mutableListOf<AppInfo>()

                for (app in apps) {
                    // Skip launcher sendiri
                    if (app.activityInfo.packageName == packageName) continue

                    val appInfo = AppInfo(
                        label = app.loadLabel(pm).toString(),
                        packageName = app.activityInfo.packageName,
                        icon = app.loadIcon(pm)
                    )
                    tempList.add(appInfo)
                }

                // Sort di background
                tempList.sortBy { it.label.lowercase() }
                
                // Update UI di main thread
                withContext(Dispatchers.Main) {
                    appList.clear()
                    appList.addAll(tempList)
                    appAdapter.notifyDataSetChanged()
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        // Back ke HomeActivity
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mainScope.cancel()  // Cancel coroutines
    }
}