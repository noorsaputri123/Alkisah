package com.rie.alkisah.Screens.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rie.alkisah.R
import com.rie.alkisah.database.db.MrKisahResRoom
import com.rie.alkisah.databinding.ActivityMrDetailBinding

class MrDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMrDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMrDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_cerita)

        setupView()
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<MrKisahResRoom>(EXTRA_STATE)
        story?.let {
            loadImage(it.photoUrl)
            setStoryDetails(it)
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_orang)
            .error(R.drawable.ic_orang)
            .into(binding.ivDetailPhoto)
    }

    private fun setStoryDetails(story: MrKisahResRoom) {
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_STATE = "extra_state"
        const val EXTRA_PHOTO = "extra_photo"
    }
}
