package com.android.itixapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.itixapp.model.Movie
import com.android.itixapp.R

class MovieAdapter(
    private var movies: List<Movie>,
    private val onFavorite: (Movie) -> Unit,
    private var favoriteTitles: Set<String> = emptySet()
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    fun updateList(newList: List<Movie>) {
        movies = newList
        notifyDataSetChanged()
    }

    fun updateFavorites(newFavorites: Set<String>) {
        favoriteTitles = newFavorites
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

            val context = itemView.context
            val resId = context.resources.getIdentifier(
                movie.imageRes, "drawable", context.packageName
            )
            imgMovie.setImageResource(if (resId != 0) resId else android.R.color.darker_gray)

            val isFavorite = favoriteTitles.contains(movie.title)
            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border
            )

            btnFavorite.setOnClickListener {
                onFavorite(movie)
                // Tambah secara lokal agar langsung berubah
                favoriteTitles = favoriteTitles + movie.title
                notifyItemChanged(adapterPosition)
            }
        }
    }
}