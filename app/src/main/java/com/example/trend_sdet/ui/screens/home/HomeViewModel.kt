package com.example.trend_sdet.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val collections: List<Collection> = emptyList(),
    val products: List<Product> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = favoriteRepository.getAllFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            val productsDeferred = async { productRepository.getProducts() }
            val collectionsDeferred = async { productRepository.getCollections() }

            val productsResult = productsDeferred.await()
            val collectionsResult = collectionsDeferred.await()

            if (productsResult.isFailure && collectionsResult.isFailure) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = productsResult.exceptionOrNull()?.message ?: "Failed to load",
                )
            } else {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    products = productsResult.getOrDefault(emptyList()),
                    collections = collectionsResult.getOrDefault(emptyList()),
                )
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(product)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val productsDeferred = async { productRepository.getProducts() }
            val collectionsDeferred = async { productRepository.getCollections() }

            val productsResult = productsDeferred.await()
            val collectionsResult = collectionsDeferred.await()

            _uiState.value = HomeUiState(
                isLoading = false,
                products = productsResult.getOrDefault(_uiState.value.products),
                collections = collectionsResult.getOrDefault(_uiState.value.collections),
            )

            _isRefreshing.value = false
        }
    }
}
