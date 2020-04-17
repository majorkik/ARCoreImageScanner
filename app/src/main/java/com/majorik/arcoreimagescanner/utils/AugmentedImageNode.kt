package com.majorik.arcoreimagescanner.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import coil.api.load
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.majorik.arcoreimagescanner.R
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.view_marker.view.*
import java.io.File
import java.util.concurrent.CompletableFuture

class AugmentedImageNode(private val context: Context?, filename: String?) : AnchorNode() {

    var image: AugmentedImage? = null
        private set

    fun setImage(image: AugmentedImage, title: String, path: String, date: String) {
        this.image = image
        anchor = image.createAnchor(image.centerPose)

        ViewRenderable.builder()
            .setView(context, R.layout.view_marker)
            .build()
            .thenAccept {
                val node = Node()
                val pose = Pose.makeTranslation(0.0f, 0.0f, 0.0f)
                node.setParent(this)
                node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())
                node.localScale = Vector3(0.2f, 0.2f, 0.2f)
                node.renderable = modelFuture!!.getNow(null)
                node.renderable = it

                it.view.title.text = title
                it.view.date.text = date
//                it.view.image.load(File(path))
            }
    }

    companion object {
        private var modelFuture: CompletableFuture<ModelRenderable?>? = null
    }

    init {
        if (modelFuture == null) {
            modelFuture =
                ModelRenderable.builder().setRegistryId("modelFuture")
                    .setSource(context, Uri.parse(filename))
                    .build()
        }
    }
}