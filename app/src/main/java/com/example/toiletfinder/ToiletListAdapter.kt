package com.example.toiletfinder

import android.location.Geocoder
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toiletfinder.network.Toilet
import java.util.Locale

class ToiletListAdapter(
    private var toilets: List<Toilet>,
    private var userLocation: Location?,
    private val onItemClick: (Toilet) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = false

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_TOILET = 1
        private const val VIEW_TYPE_LOADING = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOILET -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.toilet_list_item, parent, false)
                ToiletViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_toilet_list, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_toilet_list, parent, false)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ToiletViewHolder) {
            val toilet = toilets[position]
            holder.bind(toilet)
            holder.itemView.setOnClickListener { onItemClick(toilet) }
        }
    }

    override fun getItemCount(): Int = if (isLoading || toilets.isEmpty()) 1 else toilets.size

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> VIEW_TYPE_LOADING
            toilets.isEmpty() -> VIEW_TYPE_EMPTY
            else -> VIEW_TYPE_TOILET
        }
    }

    fun showLoading(loading: Boolean) {
        isLoading = loading
        notifyDataSetChanged()
    }

    fun updateToilets(newToilets: List<Toilet>) {
        toilets = newToilets
        notifyDataSetChanged()
    }

    fun updateUserLocation(newUserLocation: Location) {
        userLocation = newUserLocation
        notifyDataSetChanged()
    }

    inner class ToiletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.toilet_name)
        private val distanceTextView: TextView = itemView.findViewById(R.id.toilet_distance)
        private val walkTimeTextView: TextView = itemView.findViewById(R.id.walk_time_text_view)
        private val bikeTimeTextView: TextView = itemView.findViewById(R.id.bike_time_text_view)
        private val carTimeTextView: TextView = itemView.findViewById(R.id.car_time_text_view)
        private val toiletImageView: ImageView = itemView.findViewById(R.id.toilet_image)

        fun bind(toilet: Toilet) {
            val geocoder = Geocoder(itemView.context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(toilet.lat, toilet.lon, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val street = address.thoroughfare ?: ""
                val streetNumber = address.subThoroughfare ?: ""
                val featureName = address.featureName ?: ""

                nameTextView.text = when {
                    street.isNotEmpty() && streetNumber.isNotEmpty() -> "$street $streetNumber"
                    featureName.isNotEmpty() -> featureName
                    else -> "Unknown Location"
                }
            } else {
                nameTextView.text = "Unknown Location"
            }

            userLocation?.let { userLocation ->
                val toiletLocation = Location("").apply {
                    latitude = toilet.lat
                    longitude = toilet.lon
                }
                val distance = userLocation.distanceTo(toiletLocation)
                distanceTextView.text = if (distance < 1000) {
                    String.format("%.0f m", distance)
                } else {
                    String.format("%.2f km", distance / 1000)
                }

                // Travel time
                val walkTime = (distance / 1.4).toInt()
                val bikeTime = (distance / 5.5).toInt()
                val carTime = (distance / 13.8).toInt()

                walkTimeTextView.text = "${walkTime / 60} min"
                bikeTimeTextView.text = "${bikeTime / 60} min"
                carTimeTextView.text = "${carTime / 60} min"
            } ?: run {
                distanceTextView.text = ""
                walkTimeTextView.text = ""
                bikeTimeTextView.text = ""
                carTimeTextView.text = ""
            }

            Glide.with(itemView.context)
                .load(toilet.imageUrl)
                .placeholder(R.drawable.toilet_icon)
                .into(toiletImageView)
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}