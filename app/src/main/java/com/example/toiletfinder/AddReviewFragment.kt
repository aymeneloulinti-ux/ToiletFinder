package com.example.toiletfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class AddReviewFragment : Fragment() {

    private var toiletId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            toiletId = it.getLong(ARG_TOILET_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar: RatingBar = view.findViewById(R.id.review_rating_bar)
        val commentEditText: TextInputEditText = view.findViewById(R.id.review_comment_edit_text)
        val submitButton: Button = view.findViewById(R.id.submit_review_button)

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentEditText.text.toString()

            // Here you would normally save the review to a database or send it to a server

            Toast.makeText(requireContext(), "Review for toilet $toiletId submitted!", Toast.LENGTH_SHORT).show()

            // Optionally, navigate back to the previous screen
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_TOILET_ID = "toilet_id"

        fun newInstance(toiletId: Long): AddReviewFragment {
            val fragment = AddReviewFragment()
            val args = Bundle()
            args.putLong(ARG_TOILET_ID, toiletId)
            fragment.arguments = args
            return fragment
        }
    }
}
