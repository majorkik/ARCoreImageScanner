package com.majorik.arcoreimagescanner.ui.library

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.adapters.GridImagesAdapter
import com.majorik.arcoreimagescanner.data.model.Image
import com.majorik.arcoreimagescanner.ui.add_image_dialog.AddImageDialog
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: LibraryViewModel by viewModel()

    private var gridAdapter: GridImagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_layout.setOnRefreshListener(this)
        configureRecyclerView()
        setObservers()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchImages()
    }

    override fun onRefresh() {
        viewModel.fetchImages()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_IMAGE_DIALOG_CLOSE_CODE) {
            lifecycleScope.launchWhenResumed {
                delay(50)
                viewModel.fetchImages()
            }
        }
    }

    private fun configureRecyclerView() {
        gridAdapter = GridImagesAdapter({
            removeImageById(it)
        }, {
            openAddImageDialog(it)
        })
        images_list.adapter = gridAdapter
    }

    private fun setClickListeners() {
        btn_add_image.setOnClickListener {
            openAddImageDialog()
        }
    }

    private fun setObservers() {
        viewModel.imagesLiveData.observe(viewLifecycleOwner, Observer {
            gridAdapter?.updateImages(it)
            refresh_layout.isRefreshing = false
        })
    }

    private fun removeImageById(image: Image) {
        viewModel.deleteImageById(image.imagePath)
    }

    private fun openAddImageDialog(image: Image? = null) {
        val dialog = AddImageDialog.newInstance(image)
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FloatingDialog)
        dialog.setTargetFragment(this, ADD_IMAGE_DIALOG_CLOSE_CODE)
        dialog.show(requireFragmentManager(), "image_dialog")
    }

    companion object {
        const val ADD_IMAGE_DIALOG_CLOSE_CODE = 4001
    }
}
