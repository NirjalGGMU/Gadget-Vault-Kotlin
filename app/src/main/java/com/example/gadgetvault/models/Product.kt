package com.example.gadgetvault.models

import java.io.Serializable

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val rating: Double = 0.0,  // New field for product rating (e.g., 4.5)
    val stock: Int = 0        // New field for stock quantity
) : Serializable