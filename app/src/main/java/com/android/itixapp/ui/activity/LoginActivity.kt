package com.android.itixapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.itixapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var textSignUp: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase Auth dan Realtime Database dengan URL sesuai region
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://itixapp-45ca5-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        textSignUp = findViewById(R.id.textSignUp)

        buttonSignIn.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi email
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Email must use @gmail.com domain", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses login Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            // Ambil data user dari Realtime Database
                            database.child("users").child(userId).get()
                                .addOnSuccessListener { dataSnapshot ->
                                    if (dataSnapshot.exists()) {
                                        val username = dataSnapshot.child("username").getValue(String::class.java)
                                        val emailFetched = dataSnapshot.child("email").getValue(String::class.java)

                                        // Intent ke HomeActivity dengan data user
                                        val intent = Intent(this, HomeActivity::class.java).apply {
                                            putExtra("user_id", userId)
                                            putExtra("username", username)
                                            putExtra("email", emailFetched)
                                        }
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        textSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}
