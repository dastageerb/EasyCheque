package com.xynotech.cv.ai.data

import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.domain.UploadCheckRepository
import com.xynotech.cv.ai.presentation.signatureverification.VerifyCheckResponse
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class UploadCheckRepositoryImpl(private val apiService: UploadCheckApiService) :
    UploadCheckRepository {

    override suspend fun processCheque(
        text: String,
        image: MultipartBody.Part
    ): Response<CheckVerificationResponse> {
        val qrCode = text.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.processCheque(qrCode, image)
    }

    override suspend fun analyseCheque(path: String): Response<VerifyCheckResponse> {

        return apiService.analyseCheque(path)
    }
}