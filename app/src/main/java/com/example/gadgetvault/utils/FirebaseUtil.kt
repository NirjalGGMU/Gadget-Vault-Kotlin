package com.example.gadgetvault.utils

import android.util.Log
import com.example.gadgetvault.models.CartItem
import com.example.gadgetvault.models.Product
import com.example.gadgetvault.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseUtil {
    private val auth: FirebaseAuth = Firebase.auth
    private val database = FirebaseDatabase.getInstance("https://gadget-vault-default-rtdb.firebaseio.com")
    private val storage = FirebaseStorage.getInstance("gadget-vault.firebasestorage.app")

    private val productsRef = database.getReference("products")
    private val usersRef = database.getReference("users")
    private val cartRef = database.getReference("carts")

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = productsRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error fetching products: ${e.message}")
            // Fallback: Return hardcoded products for testing if Firebase fails
            listOf(
                Product("1", "Laptop", "High-performance laptop", 999.99, "https://picsum.photos/200", 4.5, 10),
                Product("2", "Headphones", "Noise-canceling headphones", 49.99, "https://picsum.photos/200", 4.2, 20),
                Product("3", "PS5", "Next-gen gaming console", 499.99, "https://picsum.photos/200", 4.8, 5)
            )
        }
    }

    suspend fun getUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error fetching user: ${e.message}")
            null
        }
    }

    fun getCart(callback: (List<CartItem>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(emptyList())
        cartRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                callback(cartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseUtil", "Error fetching cart: ${error.message}")
                callback(emptyList())
            }
        })
    }

    fun addToCart(item: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        cartRef.child(userId).push().setValue(item)
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error adding to cart: ${e.message}")
            }
    }

    fun clearCart() {
        val userId = auth.currentUser?.uid ?: return
        cartRef.child(userId).removeValue()
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error clearing cart: ${e.message}")
            }
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun registerUser(email: String, password: String, name: String, address: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(result.user?.uid ?: "", email, name, address)
            usersRef.child(result.user?.uid ?: "").setValue(user).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error registering user: ${e.message}")
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error logging in: ${e.message}")
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun updateUserProfile(name: String, address: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            val user = User(userId, auth.currentUser?.email ?: "", name, address)
            usersRef.child(userId).setValue(user).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error updating profile: ${e.message}")
            false
        }
    }

    suspend fun uploadProductImage(imageUri: String, productId: String): String? {
        return try {
            val storageRef = storage.reference.child("products/$productId.jpg")
            val uploadTask = storageRef.putFile(android.net.Uri.parse(imageUri)).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("FirebaseUtil", "Error uploading image: ${e.message}")
            null
        }
    }
}