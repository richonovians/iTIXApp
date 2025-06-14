package com.android.itixapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.itixapp.ui.adapter.FavouriteAdapter
import com.android.itixapp.model.Movie
import com.android.itixapp.model.MovieDisplay
import com.android.itixapp.databinding.ActivityFavouriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favoriteAdapter: FavouriteAdapter

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var userId: String? = null
    private val moviesList = mutableListOf<MovieDisplay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupListeners()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavouriteAdapter(moviesList) { movieToDelete ->
            deleteFavorite(movieToDelete)
        }

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(this@FavouriteActivity)
            adapter = favoriteAdapter
        }
    }


    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadFavorites() {
        val favRef = database.child("favorites").child(userId!!)

        favRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                moviesList.clear()

                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    movie?.let {
                        val resId = resources.getIdentifier(it.imageRes, "drawable", packageName)

                        // Buat MovieDisplay untuk dipakai adapter (karena imageRes = Int)
                        val displayItem = MovieDisplay(
                            title = it.title,
                            genre = it.genre,
                            duration = it.duration,
                            imageResId = resId
                        )
                        moviesList.add(displayItem)
                    }
                }

                favoriteAdapter.updateList(moviesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavouriteActivity", "Error loading favorites: ${error.message}")
                Toast.makeText(this@FavouriteActivity, "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFavorite(movie: MovieDisplay) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val favRef = database.child("favorites").child(userId)

        favRef.orderByChild("title").equalTo(movie.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }

                    // Hapus dari list lokal dan refresh RecyclerView
                    moviesList.remove(movie)
                    favoriteAdapter.updateList(moviesList)

                    Toast.makeText(this@FavouriteActivity, "Favorite removed", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FavouriteActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
