package com.example.trend_sdet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.trend_sdet.domain.model.Collection

@Composable
fun CategoryRow(
    collections: List<Collection>,
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.testTag("category_row"),
    ) {
        items(
            items = collections,
            key = { it.id },
        ) { collection ->
            CollectionCard(
                collection = collection,
                onClick = { onCollectionClick(collection.id) },
            )
        }
    }
}
