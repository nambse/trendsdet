package com.example.trend_sdet.ui.screens.productdetail

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductVariant
import com.example.trend_sdet.ui.components.ErrorState
import com.example.trend_sdet.ui.components.ImageCarousel
import com.example.trend_sdet.ui.components.QuantitySelector
import com.example.trend_sdet.ui.components.ShimmerEffect
import com.example.trend_sdet.ui.components.VariantSelector
import com.example.trend_sdet.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedVariant by viewModel.selectedVariant.collectAsStateWithLifecycle()
    val selectedOptions by viewModel.selectedOptions.collectAsStateWithLifecycle()
    val optionGroups by viewModel.optionGroups.collectAsStateWithLifecycle()
    val addToCartState by viewModel.addToCartState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is AddToCartState.Success -> {
                val result = snackbarHostState.showSnackbar(
                    message = "Added to cart!",
                    actionLabel = "View Cart",
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onNavigateToCart()
                }
                viewModel.resetAddToCartState()
            }
            is AddToCartState.Error -> {
                snackbarHostState.showSnackbar(
                    (addToCartState as AddToCartState.Error).message
                )
                viewModel.resetAddToCartState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.testTag("product_detail_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("detail_back_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::toggleFavorite,
                        modifier = Modifier.testTag("detail_favorite_button"),
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(
                        onClick = {
                            val product = (uiState as? UiState.Success)?.data ?: return@IconButton
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out ${product.title}!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                        },
                        modifier = Modifier.testTag("detail_share_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share product",
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (uiState is UiState.Success) {
                val priceText = selectedVariant?.price?.formatted
                    ?: (uiState as UiState.Success).data.priceRange.minPrice.formatted
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.testTag("detail_bottom_bar"),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        QuantitySelector(
                            quantity = quantity,
                            onIncrease = viewModel::increaseQuantity,
                            onDecrease = viewModel::decreaseQuantity,
                            itemId = "detail",
                            modifier = Modifier.testTag("detail_quantity_selector"),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = viewModel::addToCart,
                            enabled = selectedVariant?.availableForSale == true
                                    && addToCartState !is AddToCartState.Loading,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_add_to_cart_button"),
                        ) {
                            if (addToCartState is AddToCartState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("detail_add_to_cart_loading"),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text(
                                    text = "Add to Cart \u00B7 $priceText",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> ShimmerEffect.ProductDetailSkeleton(
                modifier = Modifier.padding(paddingValues),
            )
            is UiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.loadProduct(productId) },
                modifier = Modifier.padding(paddingValues),
            )
            is UiState.Success -> ProductDetailContent(
                product = state.data,
                selectedVariant = selectedVariant,
                selectedOptions = selectedOptions,
                optionGroups = optionGroups,
                onOptionSelected = viewModel::selectOption,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    selectedVariant: ProductVariant?,
    selectedOptions: Map<String, String>,
    optionGroups: List<OptionGroup>,
    onOptionSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ImageCarousel(images = product.images)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("detail_product_title"),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Compare at price + actual price
            val compareAtPrice = selectedVariant?.compareAtPrice
            if (compareAtPrice != null) {
                Text(
                    text = compareAtPrice.formatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = selectedVariant?.price?.formatted
                    ?: product.priceRange.minPrice.formatted,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("detail_product_price"),
            )

            // Stock indicator
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("detail_stock_indicator"),
            ) {
                val isInStock = selectedVariant?.availableForSale ?: false
                Surface(
                    color = if (isInStock) Color(0xFF4CAF50) else Color(0xFFE53935),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.size(8.dp),
                ) {}
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isInStock) "In Stock" else "Out of Stock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isInStock) Color(0xFF4CAF50) else Color(0xFFE53935),
                )
            }

            if (optionGroups.isNotEmpty() && product.variants.size > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                VariantSelector(
                    optionGroups = optionGroups,
                    selectedOptions = selectedOptions,
                    onOptionSelected = onOptionSelected,
                    modifier = Modifier.testTag("detail_variant_selector"),
                )
            }

            if (product.description.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = true) {
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                        modifier = Modifier.testTag("detail_product_description"),
                    )
                }

                if (product.description.length > 100) {
                    TextButton(
                        onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                        modifier = Modifier.testTag("detail_description_toggle"),
                    ) {
                        Text(if (isDescriptionExpanded) "Read less" else "Read more")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
