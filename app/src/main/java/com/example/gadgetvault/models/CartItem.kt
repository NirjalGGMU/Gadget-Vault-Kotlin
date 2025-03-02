package com.example.gadgetvault.models

data class CartItem(
    val productId: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
)