package com.example.gadgetvault.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetvault.databinding.ActivityCheckoutBinding
import com.example.gadgetvault.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                FirebaseUtil.clearCart()
                finish()
            }
        }
    }
}