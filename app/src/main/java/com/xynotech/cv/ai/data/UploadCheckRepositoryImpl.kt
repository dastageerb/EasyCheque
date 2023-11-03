package com.xynotech.cv.ai.data

import com.xynotech.cv.ai.domain.UploadCheckRepository
import okhttp3.MultipartBody
import retrofit2.Response

class UploadCheckRepositoryImpl(private val apiService: UploadCheckApiService)  : UploadCheckRepository{

    override suspend fun uploadCheck(text:String, image:MultipartBody.Part) : Response<Any> {
       return apiService.uploadImage(image)
    }
}