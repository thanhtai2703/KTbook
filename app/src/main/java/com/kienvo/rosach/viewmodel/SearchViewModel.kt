package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.model.BookCategory
import com.kienvo.rosach.repository.BookRepository
import com.kienvo.rosach.repository.CategoryRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {
    private val bookRepository = BookRepository()
    private val categoryRepository = CategoryRepository()

    // --- Categories State ---
    private val _categories = MutableStateFlow<List<BookCategory>>(emptyList())
    val categories: StateFlow<List<BookCategory>> = _categories.asStateFlow()

    private val _isCategoriesLoading = MutableStateFlow(false)
    val isCategoriesLoading: StateFlow<Boolean> = _isCategoriesLoading.asStateFlow()

    // --- Search State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults: StateFlow<List<Book>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _trendingSearches = MutableStateFlow<List<Book>>(emptyList())
    val trendingSearches: StateFlow<List<Book>> = _trendingSearches.asStateFlow()

    init {
        loadCategories()
        loadTrendingSearches()
        setupSearchDebounce()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isCategoriesLoading.value = true
            try {
                val result = categoryRepository.getAllCategories()
                _categories.value = result
            } catch (e: Exception) {
                // Log error
            } finally {
                _isCategoriesLoading.value = false
            }
        }
    }

    private fun loadTrendingSearches() {
        viewModelScope.launch {
            try {
                val books = bookRepository.getAllBooks()
                _trendingSearches.value = books.filter { it.rating >= 4.5 }.shuffled().take(10)
            } catch (e: Exception) {}
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Chờ 300ms sau khi ngừng gõ
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _searchResults.value = emptyList()
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private suspend fun performSearch(query: String) {
        _isSearching.value = true
        try {
            val results = bookRepository.searchBooks(query)
            _searchResults.value = results
        } catch (e: Exception) {
            _searchResults.value = emptyList()
        } finally {
            _isSearching.value = false
        }
    }
}
