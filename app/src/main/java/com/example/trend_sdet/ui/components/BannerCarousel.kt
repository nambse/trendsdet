package com.example.trend_sdet.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.trend_sdet.domain.model.Collection
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
    collections: List<Collection>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (collections.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { collections.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % collections.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_carousel"),
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick(collections[page].id) }
                    .testTag("banner_image_$page"),
            ) {
                AsyncImage(
                    model = collections[page].imageUrl,
                    contentDescription = collections[page].title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f),
                                ),
                                startY = 100f,
                            )
                        ),
                )

                // Text overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    Text(
                        text = collections[page].title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.testTag("banner_title_$page"),
                    )
                    Text(
                        text = "Shop Now",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.testTag("banner_shop_now_$page"),
                    )
                }
            }
        }

        if (collections.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(collections.size) { index ->
                    val isActive = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isActive) 24.dp else 8.dp,
                        label = "indicator_width",
                    )
                    val color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color),
                    )
                }
            }
        }
    }
}
