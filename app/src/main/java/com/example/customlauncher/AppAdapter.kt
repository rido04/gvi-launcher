package com.example.customlauncher

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

class AppAdapter(
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        val iconContainer: View = view.findViewById(R.id.iconContainer)

        init {
            view.setOnClickListener {
                onAppClick(apps[adapterPosition])
            }

            view.setOnFocusChangeListener { _, hasFocus ->
                animateIcon(hasFocus)
                if (hasFocus) {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                }
            }
        }

        private fun animateIcon(focused: Boolean) {
            val scale = if (focused) 1.25f else 1.0f
            
            iconContainer.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        fun bind(appInfo: AppInfo) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount() = apps.size
}