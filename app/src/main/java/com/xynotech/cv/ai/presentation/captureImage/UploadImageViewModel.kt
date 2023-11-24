package com.xynotech.cv.ai.presentation.captureImage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xynotech.cv.ai.data.entity.login.LoginEntity
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val uploadCheckRepository: UploadCheckRepository
) : ViewModel() {


    val signatureVerificationState: MutableSharedFlow<NetworkResource<CheckVerificationResponse>> =
        MutableStateFlow(NetworkResource.Loading())

//    fun verifySignature(bitmap: Bitmap, qrText:String) = viewModelScope.launch(Dispatchers.IO) {
//        try {
//            val multipart = createMultipartWithBitmap(bitmap)
//            val response = uploadCheckRepository.uploadCheck(qrText, multipart)
//            if (response.isSuccessful) {
//                state.value = NetworkResource.Success(response.body() )
//            } else {
//                state.value = NetworkResource.Error("Something went wrong")
//            }
//        }catch (e:Exception) {
//            state.value = NetworkResource.Error(e.message)
//            Log.d("1234", "uploadImage: "+e.message)
//        }
//    }

}