package com.xynotech.cv.ai.domain

import okhttp3.MultipartBody
import retrofit2.Response

interface UploadCheckRepository {

    suspend fun uploadCheck(text:String, image: MultipartBody.Part) : Response<Any>
}