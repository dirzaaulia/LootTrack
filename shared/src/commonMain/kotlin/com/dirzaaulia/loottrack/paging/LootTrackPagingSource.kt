package com.dirzaaulia.loottrack.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LootTrackPagingSource<T : Any>(
    private val fetchData: suspend (pageNumber: Int) -> List<T>
) {
    private var currentPage = 0
    private var isEndReached = false
    private var isCurrentlyLoading = false
    private val accumulatedItems = mutableListOf<T>()

    private val _itemsFlow = MutableStateFlow<List<T>>(emptyList())
    val itemsFlow: StateFlow<List<T>> = _itemsFlow.asStateFlow()

    private val _isLoadingPage = MutableStateFlow(false)
    val isLoadingPage: StateFlow<Boolean> = _isLoadingPage.asStateFlow()

    val isLastPage: Boolean
        get() = isEndReached

    fun reset() {
        currentPage = 0
        isEndReached = false
        isCurrentlyLoading = false
        accumulatedItems.clear()
        _itemsFlow.value = emptyList()
        _isLoadingPage.value = false
    }

    suspend fun loadNextPage() {
        if (isCurrentlyLoading || isEndReached) return
        isCurrentlyLoading = true
        _isLoadingPage.value = true

        try {
            val newItems = fetchData(currentPage)
            if (newItems.isEmpty()) {
                isEndReached = true
            } else {
                accumulatedItems.addAll(newItems)
                _itemsFlow.value = accumulatedItems.toList()
                currentPage++
            }
        } finally {
            isCurrentlyLoading = false
            _isLoadingPage.value = false
        }
    }
}
