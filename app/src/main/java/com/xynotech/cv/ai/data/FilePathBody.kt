package com.xynotech.cv.ai.data


import com.google.gson.annotations.SerializedName

data class FilePathBody(
    @SerializedName("file-path")
    var filePath: String
)