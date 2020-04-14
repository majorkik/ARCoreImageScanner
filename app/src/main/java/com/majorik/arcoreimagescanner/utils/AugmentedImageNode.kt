package com.majorik.arcoreimagescanner.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import java.util.concurrent.CompletableFuture

class AugmentedImageNode(context: Context?, filename: String?) : AnchorNode() {

    var image: AugmentedImage? = null
        private set

    fun setImage(image: AugmentedImage) {
        this.image = image
        if (!modelFuture!!.isDone) {
            CompletableFuture.allOf(modelFuture)
                .thenAccept { aVoid: Void? ->
                    setImage(
                        image
                    )
                }.exceptionally { throwable: Throwable? ->
                    Log.e(
                        TAG,
                        "Exception loading",
                        throwable
                    )
                    null
                }
        }
        anchor = image.createAnchor(image.centerPose)
        val node = Node()
        val pose = Pose.makeTranslation(0.0f, 0.0f, 0.0f)
        node.setParent(this)
        node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())
        node.localScale = Vector3(0.1f, 0.1f,0.1f)
        node.localRotation = Quaternion(
            pose.qx(),
            pose.qy(),
            pose.qz(),
            pose.qw()
        )
        node.renderable = modelFuture!!.getNow(null)
    }

    companion object {
        private const val TAG = "AugmentedImageNode"
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