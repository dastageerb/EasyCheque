package com.xynotech.cv.ai.presentation.captureImage

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CaptureSharedViewModel @Inject constructor() : ViewModel() {

    var capturedBitmap:Bitmap? = null

}