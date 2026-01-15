package com.example.customlauncher

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

class AppAdapter(
    private val appList: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        
        fun bind(appInfo: AppInfo) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.label
            
            itemView.setOnClickListener {
                onAppClick(appInfo)
            }
            
            // Fokus untuk remote TV
            itemView.isFocusable = true
            itemView.isFocusableInTouchMode = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int = appList.size
}