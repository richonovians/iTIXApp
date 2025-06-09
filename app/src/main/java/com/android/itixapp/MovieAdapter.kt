package com.android.itixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private var movies: List<Movie>,
    private val onClick: (Movie) -> Unit,
    private val onFavorite: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    fun updateList(newList: List<Movie>) {
        movies = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgMovie: ImageView = view.findViewById(R.id.imageMovie)
        private val txtTitle: TextView = view.findViewById(R.id.textTitle)
        private val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)

        fun bind(movie: Movie) {
            txtTitle.text = movie.title

            // Ambil resource gambar dari nama string
            val context = itemView.context
            val resId = context.resources.getIdentifier(
                movie.imageRes, "drawable", context.packageName
            )
            imgMovie.setImageResource(if (resId != 0) resId else android.R.color.darker_gray)

            // Klik pada seluruh item
            itemView.setOnClickListener {
                onClick(movie)
            }

            // Klik pada ikon favorit
            btnFavorite.setOnClickListener {
                onFavorite(movie)
                btnFavorite.setImageResource(R.drawable.ic_star) // Ganti ikon favorit
            }
        }
    }
}