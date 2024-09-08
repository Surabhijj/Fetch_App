package com.example.fetchapp.viewModel


import ItemsRepositoryImplementation
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchapp.constants.Constants
import com.example.fetchapp.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemsViewModel: ViewModel() {

    private val repository = ItemsRepositoryImplementation
    // Using MutableStateFlow for the list of items. Initially empty.
    private val _items = MutableStateFlow<List<Item>?>(null)
    val items: StateFlow<List<Item>?> = _items.asStateFlow()

    // StateFlow to track loading state remains the same
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // MutableStateFlow for holding grouped items by their listId.
    private val _groupedItems = MutableStateFlow<Map<Int, List<Item>>>(emptyMap())
    val groupedItems: StateFlow<Map<Int, List<Item>>> = _groupedItems.asStateFlow()
    init {
        // Initial data fetch operations.
        fetchItems()
    }

   private fun fetchItems() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.fetchItems()
                result.onSuccess { fetchedItems ->
                    val filteredItems = fetchedItems.filterNot { it.name.isNullOrEmpty() }
                    _items.value = filteredItems
                    Log.e("ItemsViewModel", "Items fetched and filtered successfully. Item count: ${filteredItems.size}")
                    fetchAndGroupItems(filteredItems)
                }.onFailure { exception ->
                    Log.e("ItemsViewModel", "Error fetching items: ${exception.message}", exception)
                }
            } catch (e: Exception) {
                Log.e("ItemsViewModel", "Exception in fetchItems: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchAndGroupItems(items: List<Item>) {
        // No need to fetch again, use the items passed as parameter
        val sortedFilterItems = items.sortedBy { it.listId }
        val groupedAndSortedByListId = sortedFilterItems.groupBy { it.listId }.mapValues { entry ->
            entry.value.sortedBy { it.id }
        }
        _groupedItems.value = groupedAndSortedByListId
    }

    // Sorts items based on a given criteria
    fun sortItemsBy(sortCriteria: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val sortedList = when (sortCriteria) {
                Constants.LIST_ID -> _items.value?.sortedBy { it.listId }
                Constants.ID -> _items.value?.sortedBy { it.id }
                Constants.NAME -> _items.value?.sortedBy { it.name?.lowercase() }
                else -> _items.value
            }
            _items.value = sortedList
            _isLoading.value = false

        }
    }
}
