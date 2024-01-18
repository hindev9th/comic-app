package com.cm.app.models

data class ProductModel(
    val url: String,
    val image: String,
    val name: String,
    val chapUrl1: String = "",
    val chap1: String = "",
    val chapUrl2: String = "",
    val chap2: String = ""
) {
}