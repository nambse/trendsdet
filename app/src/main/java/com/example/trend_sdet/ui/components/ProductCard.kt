package com.example.trend_sdet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.trend_sdet.domain.model.Product

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
) {
    val hasVariedPrice = product.priceRange.minPrice.amount != product.priceRange.maxPrice.amount
    val priceText = if (hasVariedPrice) {
        "From ${product.priceRange.minPrice.formatted}"
    } else {
        product.priceRange.minPrice.formatted
    }

    // Calculate discount from first variant's compareAtPrice
    val firstVariant = product.variants.firstOrNull()
    val compareAtPrice = firstVariant?.compareAtPrice
    val discountPercent = run {
        val compare = compareAtPrice ?: return@run null
        val variant = firstVariant ?: return@run null
        val compareVal = compare.amount.toDoubleOrNull() ?: 0.0
        val actualVal = variant.price.amount.toDoubleOrNull() ?: 0.0
        if (compareVal > actualVal && compareVal > 0) {
            ((compareVal - actualVal) / compareVal * 100).toInt()
        } else null
    }

    Card(
        modifier = modifier
            .testTag("product_card_${product.id}")
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.images.firstOrNull()?.url,
                    contentDescription = "Product image for ${product.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .testTag("product_image_${product.id}"),
                    contentScale = ContentScale.Crop,
                )

                // Discount badge
                if (discountPercent != null) {
                    Surface(
                        color = Color(0xFFE53935),
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .testTag("product_discount_badge_${product.id}"),
                    ) {
                        Text(
                            text = "-${discountPercent}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }

                // Favorite button
                if (onFavoriteClick != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                    ) {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("favorite_button_${product.id}"),
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("product_title_${product.id}"),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (compareAtPrice != null && discountPercent != null) compareAtPrice.formatted else "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("product_compare_price_${product.id}"),
                )
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("product_price_${product.id}"),
                )
            }
        }
    }
}
