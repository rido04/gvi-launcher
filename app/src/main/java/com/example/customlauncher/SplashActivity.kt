package com.example.customlauncher

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide system UI for immersive experience
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.splashVideo)
        
        // Set video from raw folder
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splash_video}")
        videoView.setVideoURI(videoUri)
        
        // Start video
        videoView.start()
        
        // Go to HomeActivity when video completes
        videoView.setOnCompletionListener {
            startHomeActivity()
        }
        
        // Fallback: if video fails to load, go to home after 3 seconds
        videoView.setOnErrorListener { _, _, _ ->
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                startHomeActivity()
            }, 3000)
            true
        }
    }
    
    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close splash so user can't back to it
    }
    
    override fun onBackPressed() {
        // Disable back button during splash
    }
}