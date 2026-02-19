package com.example.trend_sdet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Search(val collectionId: String? = null)

@Serializable
object Favorites

@Serializable
data class ProductDetail(val productId: String)

@Serializable
object Cart

@Serializable
object Checkout

enum class TopLevelRoute(
    val label: String,
    val icon: ImageVector,
    val route: Any,
    val testTag: String,
) {
    HOME("Home", Icons.Default.Home, Home, "bottom_nav_home"),
    SEARCH("Search", Icons.Default.Search, Search(), "bottom_nav_search"),
    FAVORITES("Favorites", Icons.Default.FavoriteBorder, Favorites, "bottom_nav_favorites"),
    CART("Cart", Icons.Default.ShoppingCart, Cart, "bottom_nav_cart"),
}
