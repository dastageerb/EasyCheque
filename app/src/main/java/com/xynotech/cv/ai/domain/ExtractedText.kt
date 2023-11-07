package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class ExtractedText(
    @SerializedName("amountInDigits")
    val amountInDigits: String,
    @SerializedName("amountInWords")
    val amountInWords: String,
    @SerializedName("checkVerified")
    val checkVerified: Boolean,
    @SerializedName("converted")
    val converted: String,
    @SerializedName("micr")
    val micr: String,
    @SerializedName("name")
    val name: String
)