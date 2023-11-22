package com.xynotech.cv.ai.presentation.details

import androidx.lifecycle.ViewModel
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.Data
import com.xynotech.cv.ai.domain.UploadCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class DetailsViewModel @Inject constructor( private val uploadCheckRepository: UploadCheckRepository) : ViewModel() {


    val _state : MutableStateFlow<CheckVerificationResponse> = MutableStateFlow(
        CheckVerificationResponse(Data("","","",
            "","","","",false), "",true)
    )

    val uiState = _state.asStateFlow()

    fun onStart(details: CheckVerificationResponse?) {
        if (details != null) {
            _state.value = details
        }
    }

}