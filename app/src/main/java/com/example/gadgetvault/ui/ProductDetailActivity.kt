package com.example.gadgetvault.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gadgetvault.databinding.ActivityProductDetailBinding
import com.example.gadgetvault.models.CartItem
import com.example.gadgetvault.models.Product
import com.example.gadgetvault.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the product from intent
        product = intent.getSerializableExtra("product") as Product

        // Set product details
        binding.tvProductName.text = product.name
        binding.tvProductDescription.text = product.description
        binding.tvProductPrice.text = "$${product.price}"
        binding.tvProductRating.text = "Rating: ${product.rating}/5 ★"
        Glide.with(this).load(product.imageUrl).into(binding.ivProductImage)

        // Set up quantity spinner
        setupQuantitySpinner()

        // Handle Add to Cart button
        binding.btnAddToCart.setOnClickListener {
            val selectedQuantity = binding.spQuantity.selectedItem.toString().toIntOrNull() ?: 1
            if (selectedQuantity <= product.stock && product.stock > 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        FirebaseUtil.addToCart(CartItem(product.id, selectedQuantity, product.price * selectedQuantity))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@ProductDetailActivity, "Failed to add to cart: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.btnAddToCart.error = "Quantity exceeds available stock (${product.stock}) or stock is depleted"
                Toast.makeText(this, "Quantity exceeds available stock (${product.stock})", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Buy Now button
        binding.btnBuyNow.setOnClickListener {
            val selectedQuantity = binding.spQuantity.selectedItem.toString().toIntOrNull() ?: 1
            if (selectedQuantity <= product.stock && product.stock > 0) {
                // Simulate buying (e.g., navigate to checkout or show confirmation)
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        FirebaseUtil.addToCart(CartItem(product.id, selectedQuantity, product.price * selectedQuantity))
                        startActivity(Intent(this@ProductDetailActivity, CheckoutActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@ProductDetailActivity, "Failed to buy: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.btnBuyNow.error = "Quantity exceeds available stock (${product.stock}) or stock is depleted"
                Toast.makeText(this, "Quantity exceeds available stock (${product.stock})", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupQuantitySpinner() {
        val maxQuantity = product.stock.coerceAtLeast(1).coerceAtMost(5) // Limit to 5 or stock, min 1
        val quantityOptions = (1..maxQuantity).map { it.toString() }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quantityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spQuantity.adapter = adapter
        binding.spQuantity.setSelection(0) // Default to 1
    }
}