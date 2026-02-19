package com.example.trend_sdet.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.ui.components.CartItemRow
import com.example.trend_sdet.ui.components.EmptyState
import com.example.trend_sdet.ui.components.ErrorState
import com.example.trend_sdet.ui.components.LoadingState
import com.example.trend_sdet.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckoutClick: () -> Unit,
    onContinueShoppingClick: () -> Unit,
    onProductClick: (String) -> Unit = {},
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadCart()
        }
    }

    Scaffold(
        modifier = Modifier.testTag("cart_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
            )
        },
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> LoadingState(
                modifier = Modifier.padding(paddingValues),
            )
            is UiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::loadCart,
                modifier = Modifier.padding(paddingValues),
            )
            is UiState.Success -> {
                val cart = state.data
                if (cart == null || cart.lines.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        EmptyState(message = "Your cart is empty")
                        OutlinedButton(
                            onClick = onContinueShoppingClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("cart_continue_shopping_button"),
                        ) {
                            Text("Continue Shopping")
                        }
                    }
                } else {
                    CartContent(
                        cart = cart,
                        onQuantityIncrease = { lineId, currentQty ->
                            viewModel.updateQuantity(lineId, currentQty + 1)
                        },
                        onQuantityDecrease = { lineId, currentQty ->
                            if (currentQty > 1) {
                                viewModel.updateQuantity(lineId, currentQty - 1)
                            }
                        },
                        onRemove = viewModel::removeItem,
                        onCheckout = onCheckoutClick,
                        onContinueShopping = onContinueShoppingClick,
                        onProductClick = onProductClick,
                        modifier = Modifier.padding(paddingValues),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartContent(
    cart: Cart,
    onQuantityIncrease: (String, Int) -> Unit,
    onQuantityDecrease: (String, Int) -> Unit,
    onRemove: (String) -> Unit,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .testTag("cart_items_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        ) {
            items(
                items = cart.lines,
                key = { it.id },
            ) { cartItem ->
                CartItemRow(
                    cartItem = cartItem,
                    onQuantityIncrease = {
                        onQuantityIncrease(cartItem.id, cartItem.quantity)
                    },
                    onQuantityDecrease = {
                        onQuantityDecrease(cartItem.id, cartItem.quantity)
                    },
                    onRemove = { onRemove(cartItem.id) },
                    onClick = if (cartItem.productId.isNotBlank()) {
                        { onProductClick(cartItem.productId) }
                    } else null,
                )
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cart_subtotal_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = cart.subtotalAmount.formatted,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("cart_subtotal_amount"),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .testTag("cart_total_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = cart.totalAmount.formatted,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("cart_total_amount"),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("cart_checkout_button"),
            ) {
                Text(
                    text = "Proceed to Checkout",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            OutlinedButton(
                onClick = onContinueShopping,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("cart_continue_shopping_button"),
            ) {
                Text("Continue Shopping")
            }
        }
    }
}
