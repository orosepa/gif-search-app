package com.example.gifsearchapp.presentation.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.FragmentGifInfoBinding
import com.example.gifsearchapp.presentation.activity.GifSearchActivity
import com.example.gifsearchapp.presentation.viewmodel.GifSearchViewModel
import com.example.gifsearchapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifInfoFragment : Fragment(R.layout.fragment_gif_info) {

    private lateinit var binding: FragmentGifInfoBinding
    private val args: GifInfoFragmentArgs by navArgs()
    private val viewModel: GifSearchViewModel by viewModels()
    private val TAG = "GifInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGifInfoBinding.inflate(inflater, container, false)
        (activity as GifSearchActivity).supportActionBar?.title = args.title
        viewModel.getGifById(args.id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentGif.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    val data = response.data!!
                    Glide.with(this)
                        .load(data.urlOriginal)
                        .listener(glideRequestListener)
                        .into(binding.ivGifInfo)

                    if (data.username.isBlank()) {
                        binding.tvAddedBy.visibility = View.GONE
                    } else {
                        binding.tvUsername.text = data.username
                    }
                    binding.tvLoadDate.text = data.importDatetime
                    binding.tvTitle.text = data.title
                    binding.ivRating.setImageResource(
                        when (data.rating) {
                            "g" -> R.drawable.mpa_rating_g
                            "pg" -> R.drawable.mpa_rating_pg
                            "pg-13" -> R.drawable.mpa_rating_pg_13
                            "r" -> R.drawable.mpa_rating_r
                            else -> 0
                        }
                    )
                }
                is Resource.Loading -> {
                    Log.i(TAG, "Loading gif data...")
                }
                is Resource.Error -> {}
            }
        }
    }

    private val glideRequestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            binding.pbGifInfo.visibility = View.GONE
            binding.gifInfoContainer.visibility = View.VISIBLE
            return false
        }
    }
}