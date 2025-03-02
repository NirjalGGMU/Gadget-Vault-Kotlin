package com.example.gadgetvault.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetvault.binding.ActivityProfileBinding
import com.example.gadgetvault.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserProfile()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            FirebaseUtil.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = FirebaseUtil.getUser()
            user?.let {
                binding.tvName.text = it.name
                binding.tvEmail.text = it.email
                binding.tvAddress.text = it.address
            } ?: run {
                binding.tvName.text = "No user logged in"
            }
        }
    }
}