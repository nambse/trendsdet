package com.example.trend_sdet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.navigation.Cart
import com.example.trend_sdet.navigation.Favorites
import com.example.trend_sdet.navigation.Home
import com.example.trend_sdet.navigation.Search
import com.example.trend_sdet.navigation.TopLevelRoute
import com.example.trend_sdet.navigation.TrendSdetNavGraph
import com.example.trend_sdet.ui.components.BottomNavBar
import com.example.trend_sdet.ui.theme.TrendSdetTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    cartRepository: CartRepository,
) : ViewModel() {
    val cartItemCount: StateFlow<Int> = cartRepository.cartItemCount
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrendSdetTheme {
                MainApp()
            }
        }
    }
}

@Composable
private fun MainApp(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isTabRoute = currentDestination?.let { dest ->
        dest.hasRoute<Home>() || dest.hasRoute<Search>() || dest.hasRoute<Favorites>() || dest.hasRoute<Cart>()
    } ?: false

    val currentTab = when {
        currentDestination?.hasRoute<Home>() == true -> TopLevelRoute.HOME
        currentDestination?.hasRoute<Search>() == true -> TopLevelRoute.SEARCH
        currentDestination?.hasRoute<Favorites>() == true -> TopLevelRoute.FAVORITES
        currentDestination?.hasRoute<Cart>() == true -> TopLevelRoute.CART
        else -> null
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
        bottomBar = {
            AnimatedVisibility(
                visible = isTabRoute,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                BottomNavBar(
                    currentRoute = currentTab,
                    cartItemCount = cartItemCount,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo<Home> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        TrendSdetNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
        )
    }
}
