package com.example.gifsearchapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.FragmentGifGalleryBinding
import com.example.gifsearchapp.presentation.adapter.GifGalleryAdapter
import com.example.gifsearchapp.presentation.viewmodel.GifSearchViewModel
import com.example.gifsearchapp.util.Resource
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifGalleryFragment : Fragment(R.layout.fragment_gif_gallery) {

    private val viewModel: GifSearchViewModel by viewModels()
    private lateinit var binding: FragmentGifGalleryBinding
    private lateinit var gifGalleryAdapter: GifGalleryAdapter
    private var offset = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGifGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.searchView.setOnQueryTextListener(queryTextListener)
        loadData()
    }

    private fun loadData() {
        viewModel.gifs.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    gifGalleryAdapter.differ.submitList(response.data?.data)
                }
                is Resource.Loading -> {}
                is Resource.Error -> {}
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
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            Thread.sleep(500)
            viewModel.searchGifs(p0 ?: "", offset)
            return true
        }

    }
}