package com.xynotech.cv.ai.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part

interface UploadImageApi {

    suspend fun uploadImage(@Part image:MultipartBody.Part) : Response<Any>
}