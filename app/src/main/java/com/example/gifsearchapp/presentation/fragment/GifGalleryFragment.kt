package com.example.gifsearchapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gifsearchapp.R
import com.example.gifsearchapp.databinding.FragmentGifGalleryBinding
import com.example.gifsearchapp.presentation.activity.GifSearchActivity
import com.example.gifsearchapp.presentation.adapter.GifGalleryAdapter
import com.example.gifsearchapp.presentation.viewmodel.GifSearchViewModel
import com.example.gifsearchapp.util.Constants
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
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

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
                if (binding.searchView.hasFocus()) {
                    offset = 0
                    viewModel.searchGifs(p0 ?: "", offset)
                }
            }
            return true
        }
    }
    private fun loadData() {
        viewModel.gifs.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    isLastPage =
                        offset >= response.data?.pagination?.totalCount!!
                    gifGalleryAdapter.differ.submitList(response.data.data)
                    gifGalleryAdapter.notifyItemRangeChanged(offset, Constants.PAGE_SIZE)
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    Log.i(TAG, "Loading gifs...")
                    showProgressBar()
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

    private fun showProgressBar() {
        binding.pbGifGallery.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.pbGifGallery.visibility = View.INVISIBLE
        binding.rvGifGallery.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE
        binding.tvErrorGallery.visibility = View.GONE
        isLoading = false
    }
    private fun setupRecyclerView() {
        gifGalleryAdapter = GifGalleryAdapter()
        binding.rvGifGallery.apply {
            adapter = gifGalleryAdapter
            layoutManager = GridLayoutManager(activity, 3)
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                offset += Constants.PAGE_SIZE
                viewModel.searchGifs(offset = offset)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}