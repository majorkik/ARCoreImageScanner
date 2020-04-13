package com.majorik.arcoreimagescanner.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.majorik.arcoreimagescanner.R

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = LibraryFragment()
    }
}
