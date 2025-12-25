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
                if (result.isNotEmpty()) {
                    _categories.value = result
                } else {
                    // Fallback to manual list if DB is empty
                    _categories.value = getFallbackCategories()
                }
            } catch (e: Exception) {
                _categories.value = getFallbackCategories()
            } finally {
                _isCategoriesLoading.value = false
            }
        }
    }

    private fun getFallbackCategories(): List<BookCategory> {
        return listOf(
            BookCategory(id = "1", name = "Sách nói", slug = "top-thinh-hanh", color = "#6D4C41", imageUrl = "https://nld.mediacdn.vn/2021/1/22/13-cay-cam-ngot-161132379604435791636.jpg"),
            BookCategory(id = "2", name = "Self-Help", slug = "sach-chua-lanh", color = "#2E7D32", imageUrl = "https://davibooks.vn/stores/uploads/z/z4729024325679_319a5b9666920fe8e785dcf3f0102996__97337_image2_800_big.jpg"),
            BookCategory(id = "3", name = "Trinh thám", slug = "tieu-thuyet-trinh-tham", color = "#BF360C", imageUrl = "https://salt.tikicdn.com/cache/w1200/ts/product/f4/e3/c2/c0397072522730248232930229342734.jpg"),
            BookCategory(id = "4", name = "Thiếu nhi", slug = "kids", color = "#D81B60", imageUrl = "https://cdn1.fahasa.com/media/flashmagazine/images/page_images/than_mong_mo_va_cuoc_chien_giac_mo/2023_05_09_16_40_10_1-390x510.jpg"),
            BookCategory(id = "5", name = "Thiên văn", slug = "astronomy", color = "#283593", imageUrl = "https://dtv-ebook.com.vn/images/files_2/2022/012022/vu-tru-carl-sagan.jpg")
        )
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
