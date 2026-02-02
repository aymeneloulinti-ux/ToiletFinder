package com.example.toiletfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ToiletListFragment : Fragment() {

    private val toiletViewModel: ToiletViewModel by activityViewModels()
    private lateinit var toiletListRecyclerView: RecyclerView
    private lateinit var toiletListAdapter: ToiletListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_toilet_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toiletListRecyclerView = view.findViewById(R.id.toilet_list_recyclerview)
        toiletListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        toiletListAdapter = ToiletListAdapter(emptyList(), null) { toilet ->
            val detailFragment = ToiletDetailBottomSheetFragment.newInstance(toilet)
            detailFragment.show(parentFragmentManager, detailFragment.tag)
        }
        toiletListRecyclerView.adapter = toiletListAdapter

        toiletViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toiletListAdapter.showLoading(isLoading)
        }

        toiletViewModel.userLocation.observe(viewLifecycleOwner) { location ->
            location?.let {
                toiletListAdapter.updateUserLocation(it)
                toiletViewModel.toilets.value?.let { toilets ->
                    val sortedToilets = toilets.sortedBy { toilet ->
                        toilet.distanceTo(it)
                    }
                    toiletListAdapter.updateToilets(sortedToilets)
                }
            }
        }

        toiletViewModel.toilets.observe(viewLifecycleOwner) { toilets ->
            if (toilets != null) {
                val sortedToilets = toiletViewModel.userLocation.value?.let { location ->
                    toilets.sortedBy { toilet ->
                        toilet.distanceTo(location)
                    }
                } ?: toilets
                toiletListAdapter.updateToilets(sortedToilets)
            }
        }
    }
}