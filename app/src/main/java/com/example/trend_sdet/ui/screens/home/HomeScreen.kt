package com.example.trend_sdet.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.ui.components.BannerCarousel
import com.example.trend_sdet.ui.components.CategoryRow
import com.example.trend_sdet.ui.components.EmptyState
import com.example.trend_sdet.ui.components.ErrorState
import com.example.trend_sdet.ui.components.ProductCard
import com.example.trend_sdet.ui.components.SectionHeader
import com.example.trend_sdet.ui.components.ShimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .testTag("home_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trend SDET",
                        modifier = Modifier.testTag("home_title"),
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> ShimmerEffect.HomeSkeletonContent(
                modifier = Modifier.padding(paddingValues),
            )
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = viewModel::loadData,
                modifier = Modifier.padding(paddingValues),
            )
            uiState.products.isEmpty() && uiState.collections.isEmpty() -> EmptyState(
                message = "No products found",
                modifier = Modifier.padding(paddingValues),
            )
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .testTag("home_pull_to_refresh"),
                ) {
                    HomeContent(
                        collections = uiState.collections,
                        products = uiState.products,
                        favoriteIds = favoriteIds,
                        onProductClick = onProductClick,
                        onCollectionClick = onCollectionClick,
                        onToggleFavorite = viewModel::toggleFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    collections: List<Collection>,
    products: List<Product>,
    favoriteIds: Set<String>,
    onProductClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    onToggleFavorite: (Product) -> Unit,
) {
    val bannerCollections = collections.filter { it.imageUrl != null }
    val productRows = products.chunked(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_content"),
    ) {
        if (bannerCollections.isNotEmpty()) {
            item(key = "banner") {
                BannerCarousel(
                    collections = bannerCollections,
                    onClick = onCollectionClick,
                )
            }
        }

        if (collections.isNotEmpty()) {
            item(key = "categories_header") {
                SectionHeader(title = "Categories")
            }
            item(key = "categories_row") {
                CategoryRow(
                    collections = collections,
                    onCollectionClick = onCollectionClick,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (products.isNotEmpty()) {
            item(key = "products_header") {
                SectionHeader(title = "Products")
            }
            items(
                items = productRows,
                key = { row -> row.first().id },
            ) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            isFavorite = favoriteIds.contains(product.id),
                            onFavoriteClick = { onToggleFavorite(product) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
