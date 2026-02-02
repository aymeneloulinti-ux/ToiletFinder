package com.example.toiletfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.toiletfinder.network.Toilet

class ToiletDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var toilet: Toilet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            toilet = it.getParcelable(ARG_TOILET)!!!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_toilet_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.toilet_name_detail).text = toilet.name
        view.findViewById<RatingBar>(R.id.toilet_rating_detail).rating = toilet.userRating ?: 0f
        view.findViewById<TextView>(R.id.toilet_comment_count_detail).text = "(${toilet.commentCount} reviews)"

        view.findViewById<Button>(R.id.directions_button_detail).setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${toilet.lat},${toilet.lon}&mode=w")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_TOILET = "toilet"

        fun newInstance(toilet: Toilet): ToiletDetailBottomSheetFragment {
            val fragment = ToiletDetailBottomSheetFragment()
            val args = Bundle()
            args.putParcelable(ARG_TOILET, toilet)
            fragment.arguments = args
            return fragment
        }
    }
}