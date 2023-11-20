package com.xynotech.cv.ai.presentation.processImage

import androidx.lifecycle.ViewModel
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.utils.NetworkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


data class ProcessViewModelUiState(var isLoading:Boolean)

@HiltViewModel
class ProcessViewModel @Inject constructor( private val uploadCheckRepository: UploadCheckRepository) : ViewModel() {


    fun onRemoveErrorState() {
        _state.value = NetworkResource.NONE()
    }


    val _state : MutableStateFlow<NetworkResource<CheckVerificationResponse>> = MutableStateFlow(NetworkResource.Loading())

    val uiState = _state.asStateFlow()

}