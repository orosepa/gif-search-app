package com.example.gifsearchapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.ItemGifGalleryBinding
import com.example.gifsearchapp.domain.model.Gif

class GifGalleryAdapter : RecyclerView.Adapter<GifGalleryAdapter.GifViewHolder>() {

    inner class GifViewHolder(
        private val binding: ItemGifGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gif: Gif) {
            Glide.with(itemView.context)
                .load(gif.urlSmall)
                .thumbnail(0.03f)
                .into(binding.ivGifItem)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Gif>() {
        override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GifViewHolder =
        GifViewHolder(
            ItemGifGalleryBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_gif_gallery,
                    parent,
                    false
                )
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Gif) -> Unit)? = null

    fun setOnItemClickListener(listener: (Gif) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val gif = differ.currentList[position]
        holder.bind(gif)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(gif) }
        }
    }
}