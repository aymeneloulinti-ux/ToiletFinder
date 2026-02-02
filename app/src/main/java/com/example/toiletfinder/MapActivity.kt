package com.example.toiletfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.toiletfinder.network.Toilet
import com.google.android.material.button.MaterialButtonToggleGroup

class MapActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val radiusToggleGroup: MaterialButtonToggleGroup = findViewById(R.id.radius_toggle_group)
        radiusToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val radius = when (checkedId) {
                    R.id.button_200m -> 200
                    R.id.button_500m -> 500
                    R.id.button_1km -> 1000
                    else -> 500
                }
                mapFragment.setRadius(radius)
            }
        }

        if (savedInstanceState == null) {
            mapFragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_fragment_container, mapFragment)
                .commit()
        }
    }

    fun showAddReview(toilet: Toilet) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, AddReviewFragment.newInstance(toilet.id))
            .addToBackStack(null)
            .commit()
    }

    fun showToiletDetail(toilet: Toilet) {
        val toiletDetailFragment = ToiletDetailBottomSheetFragment.newInstance(toilet)
        toiletDetailFragment.show(supportFragmentManager, toiletDetailFragment.tag)
    }

    fun updateRadiusButtons(searchRadius: Int) {
        val button200m: Button = findViewById(R.id.button_200m)
        val button500m: Button = findViewById(R.id.button_500m)
        val button1km: Button = findViewById(R.id.button_1km)

        button200m.text = "${searchRadius}m"
        button500m.text = "${searchRadius * 2.5}m"
        button1km.text = "${searchRadius * 5}m"
    }
}
