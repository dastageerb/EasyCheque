package com.xynotech.cv.ai.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadCheckApiService {

    @POST("upload-image")
    suspend fun uploadImage(@Part("test-image") image:MultipartBody.Part) : Response<Any>
}