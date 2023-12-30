package com.xynotech.cv.ai.presentation.captureImage.capture.barcode_overlay

import android.graphics.RectF
import com.google.mlkit.vision.barcode.common.Barcode

object PreferenceUtils {
  fun getProgressToMeetBarcodeSizeRequirement(overlay: GraphicOverlay, barcode: Barcode): Float {

    val reticleBoxWidth = getBarcodeReticleBox(overlay).width()
    val barcodeWidth = overlay.translateX(barcode.boundingBox?.width()?.toFloat() ?: 0f)
    val requiredWidth: Float =
      reticleBoxWidth * 50 / 100
    return (barcodeWidth / requiredWidth).coerceAtMost(1f)
  }

  fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
    val overlayWidth = overlay.width.toFloat()
    val overlayHeight = overlay.height.toFloat()
    val boxWidth =
      overlayWidth * 80 / 100
    val boxHeight =
      overlayHeight * 35 / 100
    val cx = overlayWidth / 2
    val cy = overlayHeight / 2
    return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
  }


//  fun getUserSpecifiedPreviewSize(context: Context): CameraSizePair? {
//    return try {
//      val previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size)
//      val pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size)
//      val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//      CameraSizePair(
//        Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)!!),
//        Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)!!)
//      )
//    } catch (e: Exception) {
//      null
//    }
//  }
//
}
