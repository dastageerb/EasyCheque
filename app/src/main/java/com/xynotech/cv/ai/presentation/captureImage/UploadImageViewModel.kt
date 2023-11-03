package com.xynotech.cv.ai.presentation.captureImage

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val uploadCheckRepository: UploadCheckRepository
) : ViewModel() {

    val state : MutableStateFlow<NetworkResource<Any>> = MutableStateFlow(NetworkResource.Loading())

    fun uploadImage(bitmap: Bitmap, qrText:String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val multipart = createMultipartWithBitmap(bitmap)
            val response = uploadCheckRepository.uploadCheck(qrText, multipart)
            if (response.isSuccessful) {
                state.value = NetworkResource.Success(response.body())
            } else {
                state.value = NetworkResource.Error("Something went wrong")
            }
        }catch (e:Exception) {
            state.value = NetworkResource.Error(e.message)
            Log.d("1234", "uploadImage: "+e.message)
        }
    }

    private fun createMultipartWithBitmap(bitmap: Bitmap): MultipartBody.Part {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapBytes = bos.toByteArray()
        val requestBody = bitmapBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, bitmapBytes.size)
        return MultipartBody.Part.createFormData("test-image", "my_bitmap.jpg", requestBody)
    }
}