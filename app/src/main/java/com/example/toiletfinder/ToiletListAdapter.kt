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
    private val toilets: List<Toilet>,
    private val userLocation: Location,
    private val onItemClick: (Toilet) -> Unit
) : RecyclerView.Adapter<ToiletListAdapter.ToiletViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.toilet_list_item, parent, false)
        return ToiletViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToiletViewHolder, position: Int) {
        val toilet = toilets[position]
        holder.bind(toilet)
        holder.itemView.setOnClickListener { onItemClick(toilet) }
    }

    override fun getItemCount(): Int = toilets.size

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

            Glide.with(itemView.context)
                .load(toilet.imageUrl)
                .placeholder(R.drawable.ic_toilet_marker)
                .into(toiletImageView)
        }
    }
}
