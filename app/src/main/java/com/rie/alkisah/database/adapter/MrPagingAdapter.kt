package com.rie.alkisah.database.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.rie.alkisah.R
import com.rie.alkisah.Screens.views.MrDetailActivity
import com.rie.alkisah.database.db.MrKisahResRoom
import com.rie.alkisah.databinding.ItemListKisahBinding

class MrPagingAdapter : PagingDataAdapter<MrKisahResRoom, MrPagingAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemListKisahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(private val binding: ItemListKisahBinding) : RecyclerView.ViewHolder(binding.root) {
        fun loadImage(url: String, imageView: ImageView, placeholder: Int) {
            val maxHeight = 535 * imageView.resources.displayMetrics.density
            Glide.with(imageView.context)
                .load(url)
                .override(Target.SIZE_ORIGINAL, maxHeight.toInt())
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView)
        }
        fun bind(story: MrKisahResRoom) {
            val name = story.name.replaceFirstChar { it.uppercase() }
            val apiUrlAvatar = "https://ui-avatars.com/api/?name=$name&size=128&background=random"
            binding.apply {
                loadImage(apiUrlAvatar, profileImage, R.drawable.ic_orang)
                loadImage(story.photoUrl, ivItemPhoto, R.drawable.bg_post_image)
                tvItemName.text = name
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MrDetailActivity::class.java)
                intent.putExtra(MrDetailActivity.EXTRA_STATE, story)
                intent.putExtra(MrDetailActivity.EXTRA_PHOTO, apiUrlAvatar)
                it.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MrKisahResRoom>() {
            override fun areItemsTheSame(oldItem: MrKisahResRoom, newItem: MrKisahResRoom): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MrKisahResRoom, newItem: MrKisahResRoom): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}