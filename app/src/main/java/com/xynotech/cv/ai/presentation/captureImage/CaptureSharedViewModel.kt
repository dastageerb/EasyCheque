
package com.xynotech.cv.ai.presentation.captureImage

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.xynotech.cv.ai.presentation.captureImage.capture.CapturingFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalGetImage::class)
class CaptureSharedViewModel @Inject constructor() : ViewModel() {

    var capturedBitmap: Bitmap? = null

    var scannedQRResult: String? = null

    private var isScanning: Boolean = false


    var _scanState = Channel<Boolean>()
    val scanState = _scanState.receiveAsFlow()

    fun scanWithMlKit(imageProxy: ImageProxy)  {
            val mediaImage = imageProxy.image
            if (mediaImage != null && !isScanning) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                val scanner = BarcodeScanning.getClient()

                isScanning = true
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->

                        barcodes.firstOrNull().let { barcode ->
                            val rawValue = barcode?.rawValue
                            rawValue?.let {
                                viewModelScope.launch {
                                    if (scannedQRResult == null) {
                                       _scanState.send(true)
                                        scannedQRResult = rawValue
                                    }
                                    Log.d(CapturingFragment.TAG, "scanQR: " + scannedQRResult)

                                }
                            }
                        }

                        isScanning = false
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        Log.d(CapturingFragment.TAG, "scanWithMlKit: "+it)
                        isScanning = false
                        imageProxy.close()
                    }

                if (scannedQRResult == null) {
                    Log.d(CapturingFragment.TAG, "startCamera: ")
                }
            }
        }

}