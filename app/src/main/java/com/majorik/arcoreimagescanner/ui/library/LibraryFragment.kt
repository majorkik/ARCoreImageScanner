package com.majorik.arcoreimagescanner.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.ui.arscene.ScannerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchImages()

        setObservers()
    }

    private fun setObservers() {
        viewModel.imagesLiveData.observe(viewLifecycleOwner, Observer {

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = LibraryFragment()
    }
}
