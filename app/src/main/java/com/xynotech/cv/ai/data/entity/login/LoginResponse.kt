/*
 * Copyright © 2017-2023 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xynotech.cv.ai.data.entity.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    var accessToken: String?
)