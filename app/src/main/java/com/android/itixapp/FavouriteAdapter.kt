package com.android.itixapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.itixapp.Movie
import com.android.itixapp.databinding.ItemFavouriteBinding

class FavouriteAdapter(
    private var movies: List<MovieDisplay>
) : RecyclerView.Adapter<FavouriteAdapter.FavViewHolder>() {

    inner class FavViewHolder(private val binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieDisplay) {
            binding.imgMovie.setImageResource(movie.imageResId)
            binding.tvTitle.text = movie.title
            binding.tvGenre.text = "${movie.genre} • ${movie.duration}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = ItemFavouriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateList(newMovies: List<MovieDisplay>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
