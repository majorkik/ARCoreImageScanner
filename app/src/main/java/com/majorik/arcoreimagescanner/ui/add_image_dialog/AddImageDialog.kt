package com.majorik.arcoreimagescanner.ui.add_image_dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import coil.api.load
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.data.model.Image
import com.majorik.arcoreimagescanner.extensions.setVisibility
import com.majorik.arcoreimagescanner.ui.library.LibraryFragment.Companion.ADD_IMAGE_DIALOG_CLOSE_CODE
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_add_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddImageDialog : DialogFragment() {

    private val viewModel: AddImageViewModel by viewModel()

    private var currentPath: String? = null

    private var imagePath: String = ""
    private var imageTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            imagePath = getString(IMAGE_PATH_INTENT, "")
            imageTitle = getString(IMAGE_TITLE_INTENT, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            setGravity(Gravity.CENTER)
        }

        return inflater.inflate(R.layout.dialog_add_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (imagePath.isNotBlank()) {
//            btn_remove.setVisibility(true)
            btn_add.text = getString(R.string.dialog_add_image_save)
            image.load(File(imagePath))
            title_edit.setText(imageTitle)
        } else {
//            btn_remove.setVisibility(false)
            btn_add.text = getString(R.string.dialog_add_image_add)
        }

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        image_layout.setOnClickListener {
            ImagePicker.create(this)
                .theme(R.style.ImagePickerTheme)
                .returnMode(ReturnMode.ALL)
                .single()
                .includeVideo(false)
                .start()
        }

        btn_add.setOnClickListener {
            if (!title_edit.text.isNullOrBlank() && currentPath != null) {
                viewModel.addImage(title_edit.text.toString(), currentPath!!)
                sendCloseEvent()
                dismiss()
            }
        }

        btn_cancel.setOnClickListener {
            sendCloseEvent()
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val pickedImage = ImagePicker.getFirstImageOrNull(data)

            currentPath = pickedImage.path

            image.load(File(pickedImage.path))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendCloseEvent() {
        targetFragment?.onActivityResult(
            targetRequestCode,
            ADD_IMAGE_DIALOG_CLOSE_CODE,
            Intent()
        )
    }

    companion object {
        private const val IMAGE_TITLE_INTENT = "image_intent_title"
        private const val IMAGE_PATH_INTENT = "image_intent_path"

        @JvmStatic
        fun newInstance(image: Image? = null) = AddImageDialog().apply {
            arguments = Bundle().apply {
                putString(IMAGE_TITLE_INTENT, image?.title ?: "")
                putString(IMAGE_PATH_INTENT, image?.imagePath ?: "")
            }
        }
    }
}