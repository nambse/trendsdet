package com.example.trend_sdet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.trend_sdet.domain.model.ProductImage

@Composable
fun ImageCarousel(
    images: List<ProductImage>,
    modifier: Modifier = Modifier,
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("image_carousel"),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            AsyncImage(
                model = images[page].url,
                contentDescription = images[page].altText ?: "Product image ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .testTag("carousel_image_$page"),
                contentScale = ContentScale.Crop,
            )
        }

        // Page counter pill
        if (images.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("image_counter"),
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${images.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .testTag("carousel_indicators"),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(images.size) { index ->
                    val color = if (pagerState.currentPage == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                            .testTag("carousel_indicator_$index"),
                    )
                }
            }
        }
    }
}
