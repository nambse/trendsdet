package com.example.trend_sdet.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.trend_sdet.ui.components.EmptyState
import com.example.trend_sdet.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onProductClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.testTag("favorites_screen"),
        topBar = {
            TopAppBar(title = { Text("Favorites") })
        },
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            EmptyState(
                message = "No favorites yet\nTap the heart icon on products you love",
                modifier = Modifier.padding(paddingValues),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("favorites_product_grid"),
            ) {
                items(
                    items = favorites,
                    key = { it.id },
                ) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        isFavorite = favoriteIds.contains(product.id),
                        onFavoriteClick = { viewModel.removeFavorite(product.id) },
                    )
                }
            }
        }
    }
}
