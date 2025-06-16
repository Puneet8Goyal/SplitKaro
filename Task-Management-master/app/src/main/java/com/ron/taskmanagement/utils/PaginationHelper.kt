package com.ron.taskmanagement.utils

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//Pagination
class PaginationHelper{
    private var loadNExtPage: ((Int) -> Unit)? = null
    private var layoutManager: LinearLayoutManager? = null
    private var totalPagesFound: Int = 1
    private var initializationDone = false
    fun create(
        layoutManager: LinearLayoutManager,
        loadNExtPage: (Int) -> Unit
    ) {
        this.layoutManager = layoutManager
        this.loadNExtPage = loadNExtPage
        initializationDone = true
    }

    fun setTotalPages(value: Int) {
        totalPagesFound = value
    }
    fun setCurrentPages(value: Int) {
        pageListCont = value
    }


    fun getCurrentPage() = pageListCont

    private var pageListCont = 1
    private var isLoading = false
    private var isScrolling = false
    private var isLast = false

    val listScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (initializationDone) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }

            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (initializationDone) {

//            val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
                val firstViewItemPosition = layoutManager?.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager?.childCount
                val totalItemCount = layoutManager?.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLast
                val isAtLastItem =
                    (firstViewItemPosition ?: 0) + (visibleItemCount ?: 0) >= (totalItemCount ?: 0)
                val isNotBeginning = (firstViewItemPosition ?: 0) >= 0
                val shouldPaginate =
                    isNotLoadingAndNotLastPage && isAtLastItem && isNotBeginning && isScrolling

                if (shouldPaginate) {
                    if (pageListCont < totalPagesFound) {

                        pageListCont += 1
                        loadNExtPage?.let { it(pageListCont) }

                    }
                    isLoading = false
                    isScrolling = false
                    isLast = false
                }
            }
        }
    }


}