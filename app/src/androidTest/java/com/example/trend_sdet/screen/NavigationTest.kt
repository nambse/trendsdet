package com.example.trend_sdet.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.trend_sdet.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationTabsSwitchContent() {
        // Start on Home
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Navigate to Search
        composeTestRule.onNodeWithTag("bottom_nav_search").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("search_screen").assertIsDisplayed()

        // Navigate to Favorites
        composeTestRule.onNodeWithTag("bottom_nav_favorites").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("favorites_screen").assertIsDisplayed()

        // Navigate to Cart
        composeTestRule.onNodeWithTag("bottom_nav_cart").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("cart_screen").assertIsDisplayed()

        // Back to Home
        composeTestRule.onNodeWithTag("bottom_nav_home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }
}
