package com.xynotech.cv.ai.presentation.signatureverification

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

sealed class SignatureVerificationResponse<T>(
    private val data: T? = null,
    private val msg: String? = null
) {
    class Navigate<T>() : SignatureVerificationResponse<T>()
    class ApiSuccess<T>(val data: T?) : SignatureVerificationResponse<T>(data)
    class Error<T>(val msg: String?) : SignatureVerificationResponse<T>(null, msg)
    class Loading<T> : SignatureVerificationResponse<T>()

    class NONE<T> : SignatureVerificationResponse<T>()

    class SUBMITTING<T>  : SignatureVerificationResponse<T>()
}

@HiltViewModel
class SignatureVerificationViewModel @Inject constructor(private val uploadCheckRepository: UploadCheckRepository) : ViewModel() {

    fun onRemoveErrorState() {
        _state.value = SignatureVerificationResponse.NONE()
    }

    fun analyseCheck(filePath:String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = uploadCheckRepository.analyseCheque(filePath)
            Log.d("1234", "analyseCheck: $filePath ->  "+response)
            if (response.isSuccessful) {
                _state.value = SignatureVerificationResponse.ApiSuccess(response.body())
            } else {
                Log.d("1234", "analyseCheck: error ->  "+response.errorBody().toString())

                _state.value = SignatureVerificationResponse.Error("Something went wrong")
            }
        }catch (e:Exception) {
            Log.d("1234", "analyseCheck: exception" +e.message)
            _state.value = SignatureVerificationResponse.Error(e.message)
            Log.d("1234", "uploadImage: "+e.message)
        }
    }

    fun mockApi() = viewModelScope.launch {
        try {
            _state.value = SignatureVerificationResponse.SUBMITTING()
            delay(2000)
            _state.value = SignatureVerificationResponse.Navigate()

        }catch (e:Exception) {
            _state.value = SignatureVerificationResponse.Error(e.message)
            Log.d("1234", "uploadImage: "+e.message)
        }
    }

    val _state : MutableStateFlow<SignatureVerificationResponse<VerifyCheckResponse>> = MutableStateFlow(SignatureVerificationResponse.Loading())

    val uiState = _state.asStateFlow()

}