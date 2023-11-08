package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class CheckVerificationResponse(
    @SerializedName("comparison")
    val comparison: Comparison,
    @SerializedName("message")
    val message: String
)