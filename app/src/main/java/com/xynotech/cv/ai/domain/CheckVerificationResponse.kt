package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class CheckVerificationResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)