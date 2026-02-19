package com.example.trend_sdet.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trend_sdet.domain.model.PriceFilter
import com.example.trend_sdet.domain.model.SortOption
import com.example.trend_sdet.ui.components.EmptyState
import com.example.trend_sdet.ui.components.ErrorState
import com.example.trend_sdet.ui.components.FilterSortSheet
import com.example.trend_sdet.ui.components.ProductCard
import com.example.trend_sdet.ui.components.SearchBar
import com.example.trend_sdet.ui.components.ShimmerEffect
import com.example.trend_sdet.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onProductClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val selectedCollection by viewModel.selectedCollection.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val priceFilter by viewModel.priceFilter.collectAsStateWithLifecycle()
    val showFilterSheet by viewModel.showFilterSheet.collectAsStateWithLifecycle()

    if (showFilterSheet) {
        FilterSortSheet(
            currentSort = sortOption,
            currentPriceFilter = priceFilter,
            onSortSelected = viewModel::updateSortOption,
            onPriceFilterChanged = viewModel::updatePriceFilter,
            onDismiss = viewModel::dismissFilterSheet,
        )
    }

    Scaffold(
        modifier = Modifier.testTag("search_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                actions = {
                    IconButton(
                        onClick = viewModel::toggleFilterSheet,
                        modifier = Modifier.testTag("search_filter_button"),
                    ) {
                        Text(text = "\u2630")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateQuery,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // Active filter chips
            val hasActiveFilters = sortOption != SortOption.RELEVANCE ||
                    priceFilter.minPrice > 0.0 ||
                    priceFilter.maxPrice < Double.MAX_VALUE
            if (hasActiveFilters && searchQuery.isNotBlank()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp),
                ) {
                    if (sortOption != SortOption.RELEVANCE) {
                        item {
                            AssistChip(
                                onClick = { viewModel.updateSortOption(SortOption.RELEVANCE) },
                                label = { Text(sortOption.displayLabel) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Clear sort")
                                },
                            )
                        }
                    }
                    if (priceFilter.minPrice > 0.0 || priceFilter.maxPrice < Double.MAX_VALUE) {
                        item {
                            val maxLabel = if (priceFilter.maxPrice >= Double.MAX_VALUE / 2) "1000+"
                            else "${"$"}${priceFilter.maxPrice.toInt()}"
                            AssistChip(
                                onClick = { viewModel.updatePriceFilter(PriceFilter()) },
                                label = { Text("${"$"}${priceFilter.minPrice.toInt()} - $maxLabel") },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "Clear price filter")
                                },
                            )
                        }
                    }
                }
            }

            if (collections.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag("search_collection_chips"),
                ) {
                    items(
                        items = collections,
                        key = { it.id },
                    ) { collection ->
                        FilterChip(
                            selected = selectedCollection?.id == collection.id,
                            onClick = {
                                viewModel.selectCollection(
                                    if (selectedCollection?.id == collection.id) null
                                    else collection
                                )
                            },
                            label = { Text(collection.title) },
                            modifier = Modifier.testTag("search_chip_${collection.id}"),
                        )
                    }
                }
            }

            when (val state = uiState) {
                is UiState.Loading -> ShimmerEffect.SearchSkeletonContent()
                is UiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = viewModel::retry,
                )
                is UiState.Success -> {
                    if (state.data.isEmpty() && searchQuery.isNotBlank()) {
                        EmptyState(message = "No products found for \"$searchQuery\"")
                    } else if (state.data.isEmpty()) {
                        EmptyState(message = "Start typing to search products")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("search_product_grid"),
                        ) {
                            items(
                                items = state.data,
                                key = { it.id },
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    isFavorite = favoriteIds.contains(product.id),
                                    onFavoriteClick = { viewModel.toggleFavorite(product) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
