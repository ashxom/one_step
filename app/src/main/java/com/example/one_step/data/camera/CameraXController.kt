package com.example.one_step.data.camera

import android.content.Context
import android.net.Uri
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** Keeps CameraX setup and capture details outside of the Compose UI. */
class CameraXController(context: Context) {
    private val appContext = context.applicationContext
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val providerFuture = ProcessCameraProvider.getInstance(appContext)

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null

    fun bind(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onReady: (hasFlash: Boolean) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                val rotation = previewView.display?.rotation ?: Surface.ROTATION_0
                val preview = Preview.Builder().setTargetRotation(rotation).build()
                val capture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(rotation)
                    .build()

                provider.unbindAll()
                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture,
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
                cameraProvider = provider
                imageCapture = capture
                onReady(camera?.cameraInfo?.hasFlashUnit() == true)
            } catch (throwable: Throwable) {
                onError(throwable)
            }
        }, ContextCompat.getMainExecutor(appContext))
    }

    fun setFlashEnabled(enabled: Boolean) {
        camera?.cameraControl?.enableTorch(enabled)
    }

    fun capture(
        onCaptured: (Uri) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        val capture = imageCapture
        if (capture == null) {
            onError(IllegalStateException("카메라가 아직 준비되지 않았습니다."))
            return
        }

        val directory = File(appContext.cacheDir, "captures").apply { mkdirs() }
        val file = File(directory, "capture_${System.currentTimeMillis()}.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        capture.takePicture(
            options,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onCaptured(Uri.fromFile(file))
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            },
        )
    }

    fun unbind() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}
