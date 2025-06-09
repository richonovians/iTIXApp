package com.android.itixapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnLogout: ImageButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi view
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        btnBack = findViewById(R.id.btnBack)
        btnLogout = findViewById(R.id.btnLogout)

        // Inisialisasi FirebaseAuth dan DatabaseReference
        auth = FirebaseAuth.getInstance() // tanpa parameter URL
        database = FirebaseDatabase.getInstance("https://itixapp-45ca5-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("ProfileActivity", "User belum login (auth.currentUser == null)")
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            Log.d("ProfileActivity", "User ID saat ini: ${currentUser.uid}")
        }

        database.child("users").child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ProfileActivity", "Snapshot exists: ${snapshot.exists()}")
                    Log.d("ProfileActivity", "Snapshot value: ${snapshot.value}")

                    if (snapshot.exists()) {
                        val username = snapshot.child("username").getValue(String::class.java) ?: "Username tidak ditemukan"
                        val email = snapshot.child("email").getValue(String::class.java) ?: currentUser.email ?: "Email tidak ditemukan"

                        tvUsername.text = username
                        tvEmail.text = email
                    } else {
                        Toast.makeText(this@ProfileActivity, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Gagal memuat data pengguna: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}