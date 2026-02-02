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
import retrofit2.HttpException

class ToiletViewModel : ViewModel() {
    val toilets = MutableLiveData<List<Toilet>>()
    val userLocation = MutableLiveData<Location>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchToilets(latLng: LatLng, radius: Int) {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getOverpassApi().getToilets(getOverpassQuery(latLng, radius))
                val toiletsWithImages = response.toToilets().map { toilet ->
                    try {
                        val imageResponse = RetrofitInstance.getPanoramaxApi().getToiletImage("${toilet.lon - 0.001},${toilet.lat - 0.001},${toilet.lon + 0.001},${toilet.lat + 0.001}")
                        if (imageResponse.features.isNotEmpty()) {
                            toilet.copy(imageUrl = imageResponse.features.first().properties.imageUrl)
                        } else {
                            toilet
                        }
                    } catch (e: Exception) {
                        toilet // Return the original toilet if image fetching fails
                    }
                }
                toilets.postValue(toiletsWithImages)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    toilets.postValue(emptyList())
                }
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private fun getOverpassQuery(latLng: LatLng, radius: Int): String {
        return "[out:json];node(around:$radius,${latLng.latitude},${latLng.longitude})[amenity=toilets];out;"
    }
}
