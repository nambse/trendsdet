package com.example.trend_sdet.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.PriceFilter
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption
import com.example.trend_sdet.navigation.Search
import com.example.trend_sdet.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Search>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _rawUiState = MutableStateFlow<UiState<List<Product>>>(UiState.Success(emptyList()))

    private val _sortOption = MutableStateFlow(SortOption.RELEVANCE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _priceFilter = MutableStateFlow(PriceFilter())
    val priceFilter: StateFlow<PriceFilter> = _priceFilter.asStateFlow()

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    val uiState: StateFlow<UiState<List<Product>>> = combine(
        _rawUiState,
        _priceFilter,
    ) { state, filter ->
        when (state) {
            is UiState.Success -> {
                val filtered = state.data.filter { product ->
                    val price = product.priceRange.minPrice.amount.toDoubleOrNull() ?: 0.0
                    price in filter.minPrice..filter.maxPrice
                }
                UiState.Success(filtered)
            }
            else -> state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Success(emptyList()))

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections.asStateFlow()

    private val _selectedCollection = MutableStateFlow<Collection?>(null)
    val selectedCollection: StateFlow<Collection?> = _selectedCollection.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = favoriteRepository.getAllFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        _searchQuery
            .debounce(300)
            .filter { it.isNotBlank() }
            .onEach { query -> performSearch(query) }
            .launchIn(viewModelScope)

        loadCollections()
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _rawUiState.value = UiState.Success(emptyList())
        }
    }

    fun selectCollection(collection: Collection?) {
        _selectedCollection.value = collection
        if (collection != null) {
            _searchQuery.value = collection.title
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(product)
        }
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    fun updatePriceFilter(filter: PriceFilter) {
        _priceFilter.value = filter
    }

    fun toggleFilterSheet() {
        _showFilterSheet.value = !_showFilterSheet.value
    }

    fun dismissFilterSheet() {
        _showFilterSheet.value = false
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _rawUiState.value = UiState.Loading
            productRepository.searchProducts(query, sortKey = _sortOption.value)
                .onSuccess { products ->
                    _rawUiState.value = UiState.Success(products)
                }
                .onFailure { error ->
                    _rawUiState.value = UiState.Error(error.message ?: "Search failed")
                }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            productRepository.getCollections()
                .onSuccess { collectionList ->
                    _collections.value = collectionList
                    val preSelectedId = route.collectionId
                    if (preSelectedId != null) {
                        val match = collectionList.find { it.id == preSelectedId }
                        if (match != null) {
                            selectCollection(match)
                        }
                    }
                }
        }
    }

    fun retry() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }
}
