package com.example.toiletfinder

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val logo = findViewById<ImageView>(R.id.app_logo)
        val exploreButton = findViewById<Button>(R.id.explore_button)

        logo.startAnimation(fadeIn)
        exploreButton.startAnimation(fadeIn)

        exploreButton.setOnClickListener {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }
}
