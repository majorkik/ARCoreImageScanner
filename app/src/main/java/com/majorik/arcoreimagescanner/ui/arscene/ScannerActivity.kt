package com.majorik.arcoreimagescanner.ui.arscene

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.data.model.Image
import com.majorik.arcoreimagescanner.extensions.*
import com.majorik.arcoreimagescanner.utils.AugmentedImageNode
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_scanner.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.io.InputStream

class ScannerActivity : AppCompatActivity() {

    private val viewModel: ScannerViewModel by viewModel()

    private var installRequested = false

    private var session: Session? = null

    private var shouldConfigureSession = false

    private var isNotRanderable = true

    private var images: List<Image> = emptyList()

    private val augmentedImageMap: HashMap<AugmentedImage, AugmentedImageNode> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        installRequested = false
        viewModel.fetchImages()
        setObservers()

    }

    private fun setObservers() {
        viewModel.imagesLiveData.observe(this, Observer {
            images = it
            initializeSceneView()
            if (session == null) {
                var exception: Exception? = null
                var message: String? = null
                try {
                    when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                            installRequested = true
                            return@Observer
                        }
                        ArCoreApk.InstallStatus.INSTALLED -> {

                        }
                        else -> {

                        }
                    }

                    // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                    // permission on Android M and above, now is a good time to ask the user for it.
                    if (!hasCameraPermission()) {
                        requestCameraPermission()
                        return@Observer
                    }
                    session = Session( /* context = */this)
                } catch (e: UnavailableArcoreNotInstalledException) {
                    message = "Please install ARCore"
                    exception = e
                } catch (e: UnavailableUserDeclinedInstallationException) {
                    message = "Please install ARCore"
                    exception = e
                } catch (e: UnavailableApkTooOldException) {
                    message = "Please update ARCore"
                    exception = e
                } catch (e: UnavailableSdkTooOldException) {
                    message = "Please update this app"
                    exception = e
                } catch (e: Exception) {
                    message = "This device does not support AR"
                    exception = e
                }
                if (message != null) {
                    Logger.e("Exception creating session", exception)
                    return@Observer
                }
                shouldConfigureSession = true
            }
            if (shouldConfigureSession) {
                configureSession()
                shouldConfigureSession = false
                ar_scene.setupSession(session)
            }

            // Note that order matters - see the note in onPause(), the reverse applies here.
            try {
                session!!.resume()
                ar_scene.resume()
            } catch (e: CameraNotAvailableException) {
                // In some cases (such as another camera app launching) the camera may be given to
                // a different app instead. Handle this properly by showing a message and recreate the
                // session at the next iteration.
                Logger.e("Camera not available. Please restart the app.")
                session = null
                return@Observer
            }

            Logger.i("Count images (observer): ${it.size}")

        })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        if (session != null) {
            ar_scene.pause()
            session!!.pause()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!hasCameraPermission()) {
            Toast.makeText(
                this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG
            )
                .show()
            if (!shouldShowRequestPermissionRationale()) {
                // Permission denied with checking "Do not ask again".
                launchPermissionSettings()
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setFullScreenOnWindowFocusChanged(hasFocus)
    }

    private fun initializeSceneView() {
        ar_scene.scene.addOnUpdateListener { frameTime: FrameTime -> onUpdateFrame(frameTime) }
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = ar_scene.arFrame
        val updatedAugmentedImages =
            frame!!.getUpdatedTrackables(
                AugmentedImage::class.java
            )

        Logger.i("count images: ${updatedAugmentedImages.size}")

        for (augmentedImage in updatedAugmentedImages) {

            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                // Check camera image matches our reference image

                if (!augmentedImageMap.containsKey(augmentedImage)) {

                    Logger.i("aug image: ${augmentedImage.index} added")

                    val node = AugmentedImageNode(this, "car.sfb")

                    val image = images[augmentedImage.index]

                    node.setImage(augmentedImage, image.title, image.imagePath, image.date)
                    node.name = augmentedImage.index.toString()

                    augmentedImageMap[augmentedImage] = node
                    ar_scene.scene.addChild(node)
                }
            }
        }
    }

    private fun configureSession() {
        val config = Config(session)
        if (!setupAugmentedImageDb(config)) {
            Logger.e("Could not setup augmented image database")
        }
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.focusMode = Config.FocusMode.AUTO
        session!!.configure(config)
    }

    private fun setupAugmentedImageDb(config: Config): Boolean {
        val augmentedImageBitmap = loadAugmentedImage() ?: return false

        val augmentedImageDatabase = AugmentedImageDatabase(session)

        augmentedImageBitmap.forEach {
            augmentedImageDatabase.addImage("car1", it)
        }


        config.augmentedImageDatabase = augmentedImageDatabase

        return true
    }

    private fun loadAugmentedImage(): List<Bitmap>? {
        val bitmap: MutableList<Bitmap> = mutableListOf()

        try {
            images.forEach {
                bitmap.add(
                    BitmapFactory.decodeFile(it.imagePath)
                )
            }

        } catch (e: IOException) {
            Logger.e("I/O exception loading augmented image bitmap.", e)
        }

        return bitmap
    }
}
