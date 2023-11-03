package com.xynotech.cv.ai.presentation.captureImage

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.client.android.BeepManager
import com.google.zxing.common.HybridBinarizer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.nio.ByteBuffer
import javax.inject.Inject

@HiltViewModel
class CaptureSharedViewModel @Inject constructor() : ViewModel() {

    var capturedBitmap: Bitmap? = null

    var scannedQRResult: String? = null

    private var multiFormatReader: MultiFormatReader = MultiFormatReader()

    fun scanQR(activity: Activity, image: ImageProxy) {
        val beepManager = BeepManager(activity)
        if ((image.format == ImageFormat.YUV_420_888 || image.format == ImageFormat.YUV_422_888
                    || image.format == ImageFormat.YUV_444_888) && image.planes.size == 3
        ) {
            val rotatedImage = RotatedImage(getLuminancePlaneData(image), image.width, image.height)
            rotateImageArray(rotatedImage, image.imageInfo.rotationDegrees)

            val planarYUVLuminanceSource = PlanarYUVLuminanceSource(
                rotatedImage.byteArray,
                rotatedImage.width,
                rotatedImage.height,
                0, 0,
                rotatedImage.width,
                rotatedImage.height,
                false
            )
            val hybridBinarizer = HybridBinarizer(planarYUVLuminanceSource)
            val binaryBitmap = BinaryBitmap(hybridBinarizer)
            try {
                val rawResult = multiFormatReader.decodeWithState(binaryBitmap)
                if (rawResult != null) {
                    if (scannedQRResult == null) {
                        beepManager.playBeepSoundAndVibrate();
                    }
                    scannedQRResult = rawResult.text
                    Log.d("1234", "scanQR: " + scannedQRResult)
                }
            } catch (e: NotFoundException) {
                if (scannedQRResult == null) {
                    Log.w("1234", "not found ")
                }
            } finally {
                multiFormatReader.reset()
            }
        }
    }

    private fun rotateImageArray(imageToRotate: RotatedImage, rotationDegrees: Int) {
        if (rotationDegrees == 0) return // no rotation
        if (rotationDegrees % 90 != 0) return // only 90 degree times rotations

        val width = imageToRotate.width
        val height = imageToRotate.height

        val rotatedData = ByteArray(imageToRotate.byteArray.size)
        for (y in 0 until height) { // we scan the array by rows
            for (x in 0 until width) {
                when (rotationDegrees) {
                    90 -> rotatedData[x * height + height - y - 1] =
                        imageToRotate.byteArray[x + y * width] // Fill from top-right toward left (CW)
                    180 -> rotatedData[width * (height - y - 1) + width - x - 1] =
                        imageToRotate.byteArray[x + y * width] // Fill from bottom-right toward up (CW)
                    270 -> rotatedData[y + x * height] =
                        imageToRotate.byteArray[y * width + width - x - 1] // The opposite (CCW) of 90 degrees
                }
            }
        }

        imageToRotate.byteArray = rotatedData

        if (rotationDegrees != 180) {
            imageToRotate.height = width
            imageToRotate.width = height
        }
    }

    private fun getLuminancePlaneData(image: ImageProxy): ByteArray {
        val plane = image.planes[0]
        val buf: ByteBuffer = plane.buffer
        val data = ByteArray(buf.remaining())
        buf.get(data)
        buf.rewind()
        val width = image.width
        val height = image.height
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride

        // remove padding from the Y plane data
        val cleanData = ByteArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                cleanData[y * width + x] = data[y * rowStride + x * pixelStride]
            }
        }
        return cleanData
    }

    private class RotatedImage(var byteArray: ByteArray, var width: Int, var height: Int)

}