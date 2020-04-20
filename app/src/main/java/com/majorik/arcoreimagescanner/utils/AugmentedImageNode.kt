package com.majorik.arcoreimagescanner.utils

import android.content.Context
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.majorik.arcoreimagescanner.R
import kotlinx.android.synthetic.main.view_marker.view.*

class AugmentedImageNode(private val context: Context?) : AnchorNode() {

    var image: AugmentedImage? = null
        private set

    fun setImage(
        image: AugmentedImage,
        title: String,
        date: String
    ) {
        this.image = image
        this.anchor = image.createAnchor(image.centerPose)

        ViewRenderable.builder()
            .setView(context, R.layout.view_marker)
            .build()
            .thenAccept {
                val node = Node()
                node.setParent(this)
                val pose = Pose.makeTranslation(0.0f, 0.1f, 0.0f)


                node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())
                node.localScale = Vector3(0.2f, 0.2f, 0.2f)

                node.renderable = it

                scene?.addOnUpdateListener {
                    node.localRotation = scene?.camera?.localRotation
                }

                it.view.title.text = title
                it.view.date.text = date
            }
    }
}