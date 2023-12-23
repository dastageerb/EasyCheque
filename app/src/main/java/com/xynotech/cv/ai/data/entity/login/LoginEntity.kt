/*
 * Copyright © 2017-2023 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.xynotech.cv.ai.data.entity.login


import com.google.gson.annotations.SerializedName

data class LoginEntity(
    @SerializedName("new_password")
    var newPassword: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("tenant_name")
    var tenantName: String?,
    @SerializedName("username")
    var username: String?
)