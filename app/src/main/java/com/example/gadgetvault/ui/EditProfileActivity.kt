package com.example.gadgetvault.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetvault.databinding.ActivityEditProfileBinding
import com.example.gadgetvault.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserProfile()

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val address = binding.etAddress.text.toString()
            if (name.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (FirebaseUtil.updateUserProfile(name, address)) {
                        finish()
                    } else {
                        binding.etName.error = "Failed to update profile. Try again."
                    }
                }
            } else {
                binding.etName.error = "Name cannot be empty"
            }
        }
    }

    private fun loadUserProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = FirebaseUtil.getUser()
            user?.let {
                binding.etName.setText(it.name)
                binding.etAddress.setText(it.address)
            }
        }
    }
}