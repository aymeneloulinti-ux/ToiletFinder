package com.example.toiletfinder

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
    }
}