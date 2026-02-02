package com.example.toiletfinder

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.toiletfinder.ToiletViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var searchRadiusCircle: Circle? = null

    private val toiletViewModel: ToiletViewModel by activityViewModels()
    private var radius: Int = 250

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val bottomSheetContainer = view.findViewById<FrameLayout>(R.id.bottom_sheet_container)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.peekHeight = 300
        bottomSheetBehavior.isHideable = false

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, ToiletListFragment())
                .commit()
        }

        view.findViewById<FloatingActionButton>(R.id.recenter_button).setOnClickListener { recenterMap() }

        toiletViewModel.toilets.observe(viewLifecycleOwner) { toilets ->
            updateMap(toilets)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMap()
    }

    fun setRadius(newRadius: Int) {
        radius = newRadius
        toiletViewModel.userLocation.value?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            toiletViewModel.fetchToilets(currentLatLng, radius)
            drawSearchRadiusCircle(currentLatLng, radius.toDouble())
        }
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            parentFragmentManager.popBackStack()
            return
        }

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false // We have our own recenter button

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                toiletViewModel.userLocation.value = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                toiletViewModel.fetchToilets(currentLatLng, radius)
                drawSearchRadiusCircle(currentLatLng, radius.toDouble())
            }
        }
    }

    private fun updateMap(toilets: List<com.example.toiletfinder.network.Toilet>) {
        map.clear()
        // Re-draw the search radius circle since map.clear() removes it
        toiletViewModel.userLocation.value?.let {
            drawSearchRadiusCircle(LatLng(it.latitude, it.longitude), radius.toDouble())
        }
        val toiletIcon = bitmapDescriptorFromVector(R.drawable.ic_toilet_marker)
        for (toilet in toilets) {
            val toiletLocation = LatLng(toilet.lat, toilet.lon)

            map.addMarker(
                MarkerOptions()
                    .position(toiletLocation)
                    .title(toilet.name)
                    .snippet("Rating: ${toilet.userRating ?: "N/A"}")
                    .icon(toiletIcon)
            )
        }
    }

    private fun recenterMap() {
        if (toiletViewModel.userLocation.value != null) {
            val userLocation = toiletViewModel.userLocation.value!!
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(userLocation.latitude, userLocation.longitude), 16f))
        }
    }

    private fun drawSearchRadiusCircle(center: LatLng, radius: Double) {
        searchRadiusCircle?.remove()
        searchRadiusCircle = map.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.argb(128, 0, 122, 255))
                .fillColor(Color.argb(50, 0, 122, 255))
                .strokeWidth(2f)
        )
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
