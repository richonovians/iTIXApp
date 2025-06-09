package com.android.itixapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.android.itixapp.R
import com.android.itixapp.ui.adapter.SliderAdapter
import com.android.itixapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var userId: String = ""
    private var username: String = ""
    private var email: String = ""

    private lateinit var sliderAdapter: SliderAdapter
    private val sliderImages = listOf(
        R.drawable.posterlilo,
        R.drawable.dragon,
        R.drawable.dino
    )

    // === Auto Slide ===
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val sliderRunnable = object : Runnable {
        override fun run() {
            if (currentPage >= sliderImages.size) currentPage = 0
            binding.viewPager.setCurrentItem(currentPage++, true)
            sliderHandler.postDelayed(this, 3000) // 3 detik
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data user dari intent
        userId = intent.getStringExtra("user_id") ?: ""
        username = intent.getStringExtra("username") ?: ""
        email = intent.getStringExtra("email") ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupImageSlider()
        setupButtonListeners()
    }

    private fun setupImageSlider() {
        sliderAdapter = SliderAdapter(sliderImages)
        binding.viewPager.adapter = sliderAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        setupIndicatorDots()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicatorDots(position)
                currentPage = position + 1 // Reset currentPage
            }
        })
    }

    private fun setupIndicatorDots() {
        binding.indicatorLayout.removeAllViews()

        val dots = arrayOfNulls<ImageView>(sliderImages.size)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 0, 8, 0)

        for (i in sliderImages.indices) {
            dots[i] = ImageView(this).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@HomeActivity,
                        R.drawable.indicator_dot_inactive
                    )
                )
                layoutParams = params
            }
            binding.indicatorLayout.addView(dots[i])
        }
        updateIndicatorDots(0)
    }

    private fun updateIndicatorDots(position: Int) {
        val childCount = binding.indicatorLayout.childCount
        for (i in 0 until childCount) {
            val dot = binding.indicatorLayout.getChildAt(i) as ImageView
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == position) R.drawable.indicator_dot_active else R.drawable.indicator_dot_inactive
                )
            )
        }
    }

    private fun setupButtonListeners() {
        binding.btnProfile.setOnClickListener {
            try {
                val intent = Intent(this, ProfileActivity::class.java).apply {
                    putExtra("user_id", userId)
                    putExtra("username", username)
                    putExtra("email", email)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error navigating to Profile: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMovieList.setOnClickListener {
            try {
                val intent = Intent(this, MovieListActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error navigating to MovieList: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFavourite.setOnClickListener {
            try {
                val intent = Intent(this, FavouriteActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error navigating to Favourite: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // === Start & Stop Auto Slide ===
    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}
