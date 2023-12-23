package com.xynotech.cv.ai.presentation.signatureverification


import com.google.gson.annotations.SerializedName

data class VerifyCheckResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)