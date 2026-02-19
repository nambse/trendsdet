package com.example.trend_sdet.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.trend_sdet.navigation.TopLevelRoute

@Composable
fun BottomNavBar(
    currentRoute: TopLevelRoute?,
    cartItemCount: Int,
    onTabSelected: (TopLevelRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        TopLevelRoute.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == TopLevelRoute.CART && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier.testTag("cart_badge"),
                                ) {
                                    Text(cartItemCount.toString())
                                }
                            },
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                        )
                    }
                },
                label = { Text(tab.label) },
                modifier = Modifier.testTag(tab.testTag),
            )
        }
    }
}
