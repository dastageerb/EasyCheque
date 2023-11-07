
package com.xynotech.cv.ai.presentation.captureImage

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.util.VisibleForTesting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CaptureSharedViewModel @Inject constructor() : ViewModel() {

    var capturedBitmap: Bitmap? = null

    var scannedQRResult: String? = null

    private var isScanning: Boolean = false


    @VisibleForTesting
    var _scanState = Channel<Rect>()
    val scanState = _scanState.receiveAsFlow()

//    @ExperimentalGetImage
//    fun scanWithMlKit(imageProxy: ImageProxy)  {
//            val mediaImage = imageProxy.image
//            if (mediaImage != null && !isScanning) {
//                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                // Pass image to an ML Kit Vision API
//                // ...
//                val scanner = BarcodeScanning.getClient()
//
//                isScanning = true
//                scanner.process(image)
//                    .addOnSuccessListener { barcodes ->
//                        // Task completed successfully
//                        // ...
//
//
//
//                        barcodes.firstOrNull().let { barcode ->
//                            val rawValue = barcode?.rawValue
//
//                            rawValue?.let {
//                                viewModelScope.launch {
//
//                                    if (scannedQRResult == null) {
//                                        barcode.boundingBox?.let { it1 -> _scanState.send(it1) }
//                                        scannedQRResult = rawValue
//                                    }
//                                    Log.d("1234", "scanQR: " + scannedQRResult)
//
//                                }
//                            }
//                        }
//
//                        isScanning = false
//                        imageProxy.close()
//                    }
//                    .addOnFailureListener {
//                        Log.d("1234", "scanWithMlKit: "+it)
//                        isScanning = false
//                        imageProxy.close()
//                    }
//            }
//        }


//    fun scanQR(image: ImageProxy)  = viewModelScope.launch(Dispatchers.IO) {
//        if ((image.format == ImageFormat.YUV_420_888 || image.format == ImageFormat.YUV_422_888
//                    || image.format == ImageFormat.YUV_444_888) && image.planes.size == 3
//        ) {
//            val rotatedImage = RotatedImage(getLuminancePlaneData(image), image.width, image.height)
//            rotateImageArray(rotatedImage, image.imageInfo.rotationDegrees)
//
//            val planarYUVLuminanceSource = PlanarYUVLuminanceSource(
//                rotatedImage.byteArray,
//                rotatedImage.width,
//                rotatedImage.height,
//                0, 0,
//                rotatedImage.width,
//                rotatedImage.height,
//                false
//            )
//            val hybridBinarizer = HybridBinarizer(planarYUVLuminanceSource)
//            val binaryBitmap = BinaryBitmap(hybridBinarizer)
//            try {
//                val rawResult = multiFormatReader.decode(binaryBitmap)
//                if (rawResult != null) {
//                    if (scannedQRResult == null) {
//                        _scanState.send(true)
//                    }
//                    scannedQRResult = rawResult.text
//                    Log.d("1234", "scanQR: " + scannedQRResult)
//                }
//            } catch (e: NotFoundException) {
//                if (scannedQRResult == null) {
//                    Log.w("1234", "not found ")
//                }
//            } finally {
//                multiFormatReader.reset()
//            }
//        }
//    }
//
//    private fun rotateImageArray(imageToRotate: RotatedImage, rotationDegrees: Int) {
//        if (rotationDegrees == 0) return // no rotation
//        if (rotationDegrees % 90 != 0) return // only 90 degree times rotations
//
//        val width = imageToRotate.width
//        val height = imageToRotate.height
//
//        val rotatedData = ByteArray(imageToRotate.byteArray.size)
//        for (y in 0 until height) { // we scan the array by rows
//            for (x in 0 until width) {
//                when (rotationDegrees) {
//                    90 -> rotatedData[x * height + height - y - 1] =
//                        imageToRotate.byteArray[x + y * width] // Fill from top-right toward left (CW)
//                    180 -> rotatedData[width * (height - y - 1) + width - x - 1] =
//                        imageToRotate.byteArray[x + y * width] // Fill from bottom-right toward up (CW)
//                    270 -> rotatedData[y + x * height] =
//                        imageToRotate.byteArray[y * width + width - x - 1] // The opposite (CCW) of 90 degrees
//                }
//            }
//        }
//
//        imageToRotate.byteArray = rotatedData
//
//        if (rotationDegrees != 180) {
//            imageToRotate.height = width
//            imageToRotate.width = height
//        }
//    }
//
//    private fun getLuminancePlaneData(image: ImageProxy): ByteArray {
//        val plane = image.planes[0]
//        val buf: ByteBuffer = plane.buffer
//        val data = ByteArray(buf.remaining())
//        buf.get(data)
//        buf.rewind()
//        val width = image.width
//        val height = image.height
//        val rowStride = plane.rowStride
//        val pixelStride = plane.pixelStride
//
//        // remove padding from the Y plane data
//        val cleanData = ByteArray(width * height)
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                cleanData[y * width + x] = data[y * rowStride + x * pixelStride]
//            }
//        }
//        return cleanData
//    }
//
//    private class RotatedImage(var byteArray: ByteArray, var width: Int, var height: Int)

}