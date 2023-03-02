package com.example.gifsearchapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.FragmentGifGalleryBinding
import com.example.gifsearchapp.presentation.activity.GifSearchActivity
import com.example.gifsearchapp.presentation.adapter.GifGalleryAdapter
import com.example.gifsearchapp.presentation.viewmodel.GifSearchViewModel
import com.example.gifsearchapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GifGalleryFragment : Fragment(R.layout.fragment_gif_gallery) {

    private val viewModel: GifSearchViewModel by viewModels()
    private lateinit var binding: FragmentGifGalleryBinding
    private lateinit var gifGalleryAdapter: GifGalleryAdapter
    private var offset = 0

    companion object {
        const val TAG = "GifGalleryFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGifGalleryBinding.inflate(inflater, container, false)
        (activity as GifSearchActivity).supportActionBar?.setTitle(R.string.gif_search_title)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.searchView.setOnQueryTextListener(queryTextListener)
        gifGalleryAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("id", it.id)
                putString("title", it.title)
            }
            findNavController().navigate(
                R.id.action_gifGalleryFragment_to_gifInfoFragment,
                bundle
            )
        }
        loadData()
    }

    private fun loadData() {
        viewModel.gifs.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    Log.i(TAG, "Successfully loaded gifs!")
                    gifGalleryAdapter.differ.submitList(response.data?.data)
                    binding.rvGifGallery.visibility = View.VISIBLE
                    binding.searchView.visibility = View.VISIBLE
                    binding.tvErrorGallery.visibility = View.GONE
                }
                is Resource.Loading -> {
                    Log.i(TAG, "Loading gifs...")
                }
                is Resource.Error -> {
                    Log.i(TAG, "Error loading gifs!")
                    binding.rvGifGallery.visibility = View.GONE
                    binding.searchView.visibility = View.GONE
                    binding.tvErrorGallery.apply {
                        visibility = View.VISIBLE
                        text = response.message
                    }
                }
            }
        }
    }
    private fun setupRecyclerView() {
        gifGalleryAdapter = GifGalleryAdapter()
        binding.rvGifGallery.apply {
            adapter = gifGalleryAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            binding.searchView.clearFocus()
            return true
        }

        var searchJob: Job? = null
        override fun onQueryTextChange(p0: String?): Boolean {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(500L)
                if (binding.searchView.isVisible)
                    viewModel.searchGifs(p0 ?: "")
            }
            return true
        }

    }
}