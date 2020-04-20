package com.majorik.arcoreimagescanner.ui.arscene

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.FrameTime
import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.data.model.Image
import com.majorik.arcoreimagescanner.extensions.*
import com.majorik.arcoreimagescanner.utils.AugmentedImageNode
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_scanner.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class ScannerActivity : AppCompatActivity() {

    private val viewModel: ScannerViewModel by viewModel()

    private var installRequested = false

    private var session: Session? = null

    private var shouldConfigureSession = false

    private var images: List<Image> = emptyList()

    private val augmentedImageMap: HashMap<AugmentedImage, AugmentedImageNode> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        installRequested = false
        progress_title.text = getString(R.string.arscene_progress_loading_images)
        viewModel.fetchImages()
        setObservers()

    }

    private fun setObservers() {
        viewModel.imagesLiveData.observe(this, Observer {
            images = it
            progress_layout.setVisibility(false)

            Toast.makeText(this, "Images loaded (${images.count()})", Toast.LENGTH_SHORT).show()

            configureSession()
            session?.update()
        })
    }

    override fun onResume() {
        super.onResume()

        initializeSceneView()
        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
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
                    return
                }
                session = Session(this)
            } catch (e: UnavailableArcoreNotInstalledException) {
                message = getString(R.string.arscene_please_install_arcore)
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = getString(R.string.arscene_please_install_arcore)
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = getString(R.string.arscene_please_update_arcore)
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = getString(R.string.arscene_please_update_app)
                exception = e
            } catch (e: Exception) {
                message = getString(R.string.arscene_device_not_support_ar)
                exception = e
            }
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Logger.e(getString(R.string.arscene_exception_creating_session), exception)
                return
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
            Toast.makeText(
                this,
                getString(R.string.arscene_camera_not_available),
                Toast.LENGTH_SHORT
            ).show()
            session = null
            return
        }
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
                this, getString(R.string.arscene_camera_permissions), Toast.LENGTH_LONG
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
        ar_scene.scene.addOnUpdateListener { onUpdateFrame() }
    }

    private fun onUpdateFrame() {
        val frame = ar_scene.arFrame
        val updatedAugmentedImages =
            frame!!.getUpdatedTrackables(
                AugmentedImage::class.java
            )

        for (augmentedImage in updatedAugmentedImages) {

            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                // Check camera image matches our reference image

                if (!augmentedImageMap.containsKey(augmentedImage)) {
                    val node = AugmentedImageNode(this)

                    val image = images[augmentedImage.index]

                    node.setImage(augmentedImage, image.title, image.date)

                    augmentedImageMap[augmentedImage] = node
                    ar_scene.scene.addChild(node)
                }
            }
        }
    }

    private fun configureSession() {
        val config = Config(session)

        if (!setupAugmentedImageDb(config)) {
            Logger.e(getString(R.string.arscene_could_not_setup_augmented_image_database))
        }

        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.focusMode = Config.FocusMode.AUTO
        session!!.configure(config)
    }

    private fun setupAugmentedImageDb(config: Config): Boolean {
        val augmentedImageBitmap = loadAugmentedImage() ?: return false

        val augmentedImageDatabase = AugmentedImageDatabase(session)

        augmentedImageBitmap.forEachIndexed { index, item ->
            augmentedImageDatabase.addImage("image_$index", item)
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
            Logger.e(getString(R.string.arscene_exception_loading_augmented_image), e)
        }

        return bitmap
    }
}
