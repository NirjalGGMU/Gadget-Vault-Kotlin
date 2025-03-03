package com.example.gadgetvault.models

import java.io.Serializable

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val rating: Double = 0.0,  // Keeping rating for consistency (optional if not used)
    val stock: Int = 0        // Keeping stock for consistency (optional if not used)
) : Serializable