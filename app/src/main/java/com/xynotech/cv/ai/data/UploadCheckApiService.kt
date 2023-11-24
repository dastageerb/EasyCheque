package com.xynotech.cv.ai.data

import com.xynotech.cv.ai.data.entity.login.LoginEntity
import com.xynotech.cv.ai.data.entity.login.LoginResponse
import com.xynotech.cv.ai.domain.CheckVerificationResponse
import com.xynotech.cv.ai.presentation.signatureverification.VerifyCheckResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UploadCheckApiService {

    @Multipart
    @POST("process-cheque")
    suspend fun processCheque(
        @Part("qr-code") code: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<CheckVerificationResponse>


    @GET("analyze-cheque/{id}")
    suspend fun analyseCheque(
        @Path("id")checkId:String
    ): Response<VerifyCheckResponse>
}