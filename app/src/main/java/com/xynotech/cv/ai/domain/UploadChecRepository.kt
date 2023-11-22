package com.xynotech.cv.ai.domain

import com.xynotech.cv.ai.presentation.signatureverification.VerifyCheckResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface UploadCheckRepository {

    suspend fun processCheque(text:String, image: MultipartBody.Part) : Response<CheckVerificationResponse>

    suspend fun analyseCheque(path:String) : Response<VerifyCheckResponse>

}