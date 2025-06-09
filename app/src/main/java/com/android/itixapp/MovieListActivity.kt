package com.android.itixapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.itixapp.Movie
import com.android.itixapp.databinding.ActivityMovieListBinding
import com.android.itixapp.MovieAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.jvm.java

class MovieListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieListBinding
    private lateinit var nowPlayingAdapter: MovieAdapter
    private lateinit var topMovieAdapter: MovieAdapter
    private lateinit var comingSoonAdapter: MovieAdapter

    private lateinit var databaseRef: DatabaseReference
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "guest" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance("https://itixapp-45ca5-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("movies")

        // Inisialisasi adapter
        nowPlayingAdapter = MovieAdapter(emptyList(), { /* no-op */ }, { addMovieToFavorites(it) })
        topMovieAdapter = MovieAdapter(emptyList(), { /* no-op */ }, { addMovieToFavorites(it) })
        comingSoonAdapter = MovieAdapter(emptyList(), { /* no-op */ }, { addMovieToFavorites(it) })

        // Pasang adapter ke RecyclerView
        binding.rvNowPlaying.layoutManager = GridLayoutManager(this, 3)
        binding.rvNowPlaying.adapter = nowPlayingAdapter

        binding.rvTopMovie.layoutManager = GridLayoutManager(this, 3)
        binding.rvTopMovie.adapter = topMovieAdapter

        binding.rvComingSoon.layoutManager = GridLayoutManager(this, 3)
        binding.rvComingSoon.adapter = comingSoonAdapter

        // Ambil data dari Firebase
        loadMoviesFromFirebase()

        // Tombol kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUploadMovies.setOnClickListener {
            uploadMoviesToFirebase()
        }

    }

    private fun loadMoviesFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nowPlayingList = mutableListOf<Movie>()
                val topMovieList = mutableListOf<Movie>()
                val comingSoonList = mutableListOf<Movie>()

                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    movie?.let {
                        when (it.category) {
                            "Now Playing" -> nowPlayingList.add(it)
                            "Top Movie" -> topMovieList.add(it)
                            "Coming Soon" -> comingSoonList.add(it)
                        }
                    }
                }

                nowPlayingAdapter.updateList(nowPlayingList)
                topMovieAdapter.updateList(topMovieList)
                comingSoonAdapter.updateList(comingSoonList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MovieListActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMovieToFavorites(movie: Movie) {
        val favoriteRef = FirebaseDatabase.getInstance("https://itixapp-45ca5-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("favorites")
            .child(userId)

        // Gunakan title sebagai ID unik
        favoriteRef.child(movie.title).setValue(movie)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadMoviesToFirebase() {
        val databaseRef = FirebaseDatabase
            .getInstance("https://itixapp-45ca5-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("movies")

        val movies = listOf(
            Movie("Lilo & Stitch", "Now Playing", "85 min", "Animation / Sci-Fi / Comedy-Drama", "lilostitch"),
            Movie("How to Train Your Dragon", "Now Playing", "98 min", "Animation / Fantasy / Adventure", "movie_httyd"),
            Movie("F1: The Movie", "Now Playing", "120 min", "Sports / Action / Drama", "movie_f1"),
            Movie("Jurassic World: Rebirth", "Top Movie", "120 min", "Sci-Fi / Action / Adventure", "movie_jw"),
            Movie("Superman", "Top Movie", "120 min", "Superhero / Action / Sci-Fi", "movie_supr"),
            Movie("Karate Kid Legends", "Top Movie", "120 min", "Martial Arts / Drama / Family", "movie_kk"),
            Movie("WARFARE", "Coming Soon", "120 min", "War / Action / Drama", "movie_war"),
            Movie("Blood Brothers", "Coming Soon", "120 min", "Action / Thriller", "movie_brother"),
            Movie("Fear Below", "Coming Soon", "85 min", "Horror / Thriller / Adventure", "movie_fear")
        )

        var successCount = 0
        var failureCount = 0

        for (movie in movies) {
            // Validasi ringan
            if (movie.title.isBlank() || movie.imageRes.isBlank()) {
                Toast.makeText(this, "Data tidak lengkap: ${movie.title}", Toast.LENGTH_SHORT).show()
                continue
            }

            val key = databaseRef.push().key ?: movie.title.replace(" ", "_")

            databaseRef.child(key).setValue(movie)
                .addOnSuccessListener {
                    successCount++
                    if (successCount + failureCount == movies.size) {
                        Toast.makeText(this, "Upload selesai: $successCount berhasil, $failureCount gagal", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    failureCount++
                    Toast.makeText(this, "Gagal upload ${movie.title}: ${e.message}", Toast.LENGTH_SHORT).show()
                    if (successCount + failureCount == movies.size) {
                        Toast.makeText(this, "Upload selesai: $successCount berhasil, $failureCount gagal", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
