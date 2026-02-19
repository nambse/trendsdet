package com.example.trend_sdet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    itemId: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.testTag("qty_selector_$itemId"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = onDecrease,
            enabled = quantity > 1,
            modifier = Modifier
                .size(36.dp)
                .testTag("qty_minus_$itemId"),
        ) {
            Text(
                text = "\u2212",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("qty_count_$itemId"),
        )
        IconButton(
            onClick = onIncrease,
            modifier = Modifier
                .size(36.dp)
                .testTag("qty_plus_$itemId"),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase quantity",
            )
        }
    }
}
