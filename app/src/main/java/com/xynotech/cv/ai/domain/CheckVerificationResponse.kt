package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class CheckVerificationResponse(
    @SerializedName("extracted_text")
    val extractedText: ExtractedText,
    @SerializedName("message")
    val message: String,
    @SerializedName("verification")
    val verification: String
)