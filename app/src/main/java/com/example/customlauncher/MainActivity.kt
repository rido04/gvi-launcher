package com.example.customlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var appAdapter: AppAdapter
    private val appList = mutableListOf<AppInfo>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 5) // 5 kolom untuk TV
        
        loadInstalledApps()
        
        appAdapter = AppAdapter(appList) { appInfo ->
            launchApp(appInfo.packageName)
        }
        recyclerView.adapter = appAdapter
    }
    
    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        
        for (app in apps) {
            // Skip launcher sendiri
            if (app.activityInfo.packageName == packageName) continue
            
            val appInfo = AppInfo(
                label = app.loadLabel(pm).toString(),
                packageName = app.activityInfo.packageName,
                icon = app.loadIcon(pm)
            )
            appList.add(appInfo)
        }
        
        // Sort berdasarkan nama
        appList.sortBy { it.label.lowercase() }
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
        // Disable back button supaya ga keluar dari launcher
        // Atau bisa dikosongkan aja
    }
}