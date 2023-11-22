package com.xynotech.cv.ai.presentation.processImage

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class ProcessViewModel @Inject constructor( private val uploadCheckRepository: UploadCheckRepository) : ViewModel() {

    fun onRemoveErrorState() {
        _state.value = NetworkResource.NONE()
    }

    fun processCheck(bitmap: Bitmap, qrText:String) = viewModelScope.launch(Dispatchers.IO) {
        _state.value = NetworkResource.Loading()
        try {
            val multipart = createMultipartWithBitmap(bitmap)
            val response = uploadCheckRepository.processCheque(qrText, multipart)
            if (response.isSuccessful) {
                _state.value = NetworkResource.Success(response.body() )
            } else {
                val jsonObj = JSONObject(response.errorBody()?.charStream()?.readText())
                _state.value = NetworkResource.Error(jsonObj.getString("error"))
            }
        }catch (e:Exception) {
            _state.value = NetworkResource.Error(e.message)
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

    val _state : MutableStateFlow<NetworkResource<CheckVerificationResponse>> = MutableStateFlow(NetworkResource.NONE())

    val uiState = _state.asStateFlow()

}