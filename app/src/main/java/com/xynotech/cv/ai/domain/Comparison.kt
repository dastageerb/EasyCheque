package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class Comparison(
    @SerializedName("confidence")
    val confidence: Double,
    @SerializedName("extracted_text")
    val extractedText: ExtractedText,
    @SerializedName("verified")
    val verified: Boolean
)