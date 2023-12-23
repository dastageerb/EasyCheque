package com.xynotech.cv.ai.presentation.signatureverification


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("confidence")
    var confidence: Double?,
    @SerializedName("verified")
    var verified: Boolean?
)