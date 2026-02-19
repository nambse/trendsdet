package com.example.trend_sdet.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.trend_sdet.ui.screens.cart.CartScreen
import com.example.trend_sdet.ui.screens.checkout.CheckoutScreen
import com.example.trend_sdet.ui.screens.favorites.FavoritesScreen
import com.example.trend_sdet.ui.screens.home.HomeScreen
import com.example.trend_sdet.ui.screens.productdetail.ProductDetailScreen
import com.example.trend_sdet.ui.screens.search.SearchScreen

private const val TRANSITION_DURATION = 300

@Composable
fun TrendSdetNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
    ) {
        // Tab screens with fade transitions
        composable<Home>(
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
        ) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
                onCollectionClick = { collectionId ->
                    navController.navigate(Search(collectionId = collectionId)) {
                        popUpTo<Home> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        composable<Search>(
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
        ) {
            SearchScreen(
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
            )
        }

        composable<Favorites>(
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
        ) {
            FavoritesScreen(
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
            )
        }

        composable<Cart>(
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
        ) {
            CartScreen(
                onCheckoutClick = {
                    navController.navigate(Checkout)
                },
                onContinueShoppingClick = {
                    navController.navigate(Home) {
                        popUpTo<Home> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProductClick = { productId ->
                    navController.navigate(ProductDetail(productId))
                },
            )
        }

        // ProductDetail: slide in from right
        composable<ProductDetail>(
            enterTransition = {
                slideInHorizontally(tween(TRANSITION_DURATION)) { it }
            },
            exitTransition = {
                slideOutHorizontally(tween(TRANSITION_DURATION)) { it }
            },
            popEnterTransition = {
                slideInHorizontally(tween(TRANSITION_DURATION)) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(tween(TRANSITION_DURATION)) { it }
            },
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetail>()
            ProductDetailScreen(
                productId = route.productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Cart) {
                        popUpTo<Home> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        // Checkout: slide up from bottom
        composable<Checkout>(
            enterTransition = {
                slideInVertically(tween(TRANSITION_DURATION)) { it }
            },
            exitTransition = {
                slideOutVertically(tween(TRANSITION_DURATION)) { it }
            },
            popEnterTransition = {
                slideInVertically(tween(TRANSITION_DURATION)) { -it }
            },
            popExitTransition = {
                slideOutVertically(tween(TRANSITION_DURATION)) { it }
            },
        ) {
            CheckoutScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueShopping = {
                    navController.navigate(Home) {
                        popUpTo<Home> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
