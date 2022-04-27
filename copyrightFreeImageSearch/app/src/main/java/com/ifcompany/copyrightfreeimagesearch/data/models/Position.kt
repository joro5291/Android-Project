package com.ifcompany.copyrightfreeimagesearch.data.models


import com.google.gson.annotations.SerializedName

data class Position(
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null
)
