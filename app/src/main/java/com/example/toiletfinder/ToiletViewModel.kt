package com.example.toiletfinder

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toiletfinder.network.RetrofitInstance
import com.example.toiletfinder.network.Toilet
import com.example.toiletfinder.network.toToilets
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class ToiletViewModel : ViewModel() {
    val toilets = MutableLiveData<List<Toilet>>()
    val userLocation = MutableLiveData<Location>()

    fun fetchToilets(latLng: LatLng, radius: Int) {
        viewModelScope.launch {
            val response = RetrofitInstance.getOverpassApi().getToilets(getOverpassQuery(latLng, radius))
            val toiletsWithImages = response.toToilets().map { toilet ->
                val imageResponse = RetrofitInstance.getPanoramaxApi().getToiletImage("${toilet.lon - 0.001},${toilet.lat - 0.001},${toilet.lon + 0.001},${toilet.lat + 0.001}")
                if (imageResponse.features.isNotEmpty()) {
                    toilet.copy(imageUrl = imageResponse.features.first().properties.imageUrl)
                } else {
                    toilet
                }
            }
            toilets.postValue(toiletsWithImages)
        }
    }

    private fun getOverpassQuery(latLng: LatLng, radius: Int): String {
        return "[out:json];node(around:$radius,${latLng.latitude},${latLng.longitude})[amenity=toilets];out;"
    }
}
