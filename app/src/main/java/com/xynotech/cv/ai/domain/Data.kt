package com.xynotech.cv.ai.domain


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amountInDigits")
    var amountInDigits: String?,
    @SerializedName("amountInWords")
    var amountInWords: String?,
    @SerializedName("converted")
    var converted: String?,
    @SerializedName("date")
    var date: String?,
    @SerializedName("chequeId")
    var filePath: String?,
    @SerializedName("micr")
    var micr: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("verified")
    var verified: Boolean?
)