package com.example.gadgetvault.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetvault.databinding.ActivityCartBinding
import com.example.gadgetvault.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCartItems()

        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.Main).launch {
            FirebaseUtil.getCart { cartItems ->
                val total = cartItems.sumOf { it.price * it.quantity }
                binding.tvTotal.text = "Total: $$total"
            }
        }
    }
}