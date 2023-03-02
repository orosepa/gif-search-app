package com.example.gifsearchapp.presentation.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.FragmentGifInfoBinding
import com.example.gifsearchapp.domain.model.Gif
import com.example.gifsearchapp.presentation.activity.GifSearchActivity
import com.example.gifsearchapp.presentation.viewmodel.GifSearchViewModel
import com.example.gifsearchapp.util.Constants
import com.example.gifsearchapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class GifInfoFragment : Fragment(R.layout.fragment_gif_info) {

    private lateinit var binding: FragmentGifInfoBinding
    private val args: GifInfoFragmentArgs by navArgs()
    private val viewModel: GifSearchViewModel by viewModels()

    companion object {
        private const val TAG = "GifInfoFragment"
    }

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
                    val gif = response.data!!
                    setupMenu(gif)
                    showGifInfo(gif)
                }
                is Resource.Loading -> {
                    Log.i(TAG, "Loading gif data...")
                }
                is Resource.Error -> {
                    Log.i(TAG, "Error loading GifInfo! Message: ${response.message}")
                    showErrorInfo()
                }
            }
        }
    }

    private fun setupMenu(gif: Gif) {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.action_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_copy -> {
                        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("Image URL", gif.urlOriginal)
                        clipboard.setPrimaryClip(clipData)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                            Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                    }
                    android.R.id.home -> {
                        findNavController().navigateUp()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun showGifInfo(gif: Gif) {
        Glide.with(this)
            .load(gif.urlOriginal)
            .listener(glideRequestListener)
            .into(binding.ivGifInfo)

        if (gif.username.isBlank()) {
            binding.tvAddedBy.visibility = View.GONE
        } else {
            binding.tvAddedBy.text = getString(R.string.added_by, gif.username)
        }
        binding.tvImportDate.apply {
            val dateTime = LocalDateTime.parse(
                gif.importDatetime,
                DateTimeFormatter.ofPattern(Constants.IMPORT_DATE_TIME_FORMAT)
            )
            val formattedDate = DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(dateTime)
            text = getString(R.string.import_date, formattedDate)
        }
        binding.tvTitle.text = gif.title
        binding.ivRating.apply {
            setImageResource(
                when (gif.rating) {
                    "g" -> R.drawable.mpa_rating_g
                    "pg" -> R.drawable.mpa_rating_pg
                    "pg-13" -> R.drawable.mpa_rating_pg_13
                    "r" -> R.drawable.mpa_rating_r
                    else -> 0
                }
            )
            setOnClickListener {
                val stringUri = Constants.MPAA_RATING_INFO_URL + gif.rating.uppercase()
                val uri = Uri.parse(stringUri)
                val intentInfoAboutRatings = Intent(
                    Intent.ACTION_VIEW,
                    uri
                )
                startActivity(intentInfoAboutRatings)
            }
        }
    }

    private fun showErrorInfo() {
        binding.gifInfoContainer.visibility = View.GONE
        binding.pbGifInfo.visibility = View.GONE
        binding.tvErrorInfo.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.something_went_wrong)
        }
    }

    private val glideRequestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Log.i(TAG, "Error loading Gif! Message: ${e?.message}")
            binding.gifInfoContainer.visibility = View.GONE
            binding.pbGifInfo.visibility = View.GONE
            binding.tvErrorInfo.apply {
                visibility = View.VISIBLE
                text = resources.getString(R.string.something_went_wrong)
            }
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