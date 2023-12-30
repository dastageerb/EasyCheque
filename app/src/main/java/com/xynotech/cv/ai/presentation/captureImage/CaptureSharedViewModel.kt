
package com.xynotech.cv.ai.presentation.captureImage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.presentation.captureImage.capture.CapturingFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannedBarcode(val barcode: Barcode? = null)

@HiltViewModel
@OptIn(ExperimentalGetImage::class)
class CaptureSharedViewModel @Inject constructor() : ViewModel() {

    var capturedBitmap: Bitmap? = null

    var scannedQRResult: String? = null

    var filePath:String?= null

    private var isScanning: Boolean = false

    var boundingBox: Rect? = null


//    var _scanState = Channel<Barcode>()
//    val scanState = _scanState.receiveAsFlow()
//

    var _scanState = MutableStateFlow<ScannedBarcode>(ScannedBarcode())
    val scanState = _scanState.asStateFlow()


    fun scanWithMlKit(imageProxy: ImageProxy) = viewModelScope.launch(Dispatchers.IO)  {
            val mediaImage = imageProxy.image
            if (mediaImage != null && !isScanning) {
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_DATA_MATRIX
                    )
                    .build()
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient(options)
                imageProxy.close()
                isScanning = true
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull().let { barcode ->
                            val rawValue = barcode?.rawValue
                            rawValue?.let {
                                _scanState.update { it.copy(barcode = barcode) }
                                viewModelScope.launch {
                                    scannedQRResult = it
//                                    if (scannedQRResult == null) {
//                                       _scanState.send(barcode)
//                                        scannedQRResult = rawValue
//                                    }

                                _scanState.update { it.copy(barcode = barcode) }
                                }
                            }
                        }
                        isScanning = false
                    }
                    .addOnFailureListener {
                        _scanState.update { it.copy(barcode = null) }
                        isScanning = false
                    }

                if (scannedQRResult == null) {
                    Log.d(CapturingFragment.TAG, "startCamera: ")
                }
            }
        }


    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap? {
        val width = original.width
        val height = original.height
        val matrix = Matrix()
        matrix.preRotate(degrees)
        val rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true)
        val canvas = Canvas(rotatedBitmap)
        canvas.drawBitmap(original, 5.0f, 0.0f, null)
        return rotatedBitmap
    }

    var details : CheckVerificationResponse? = null


}