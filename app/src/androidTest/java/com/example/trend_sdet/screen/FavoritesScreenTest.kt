package com.example.trend_sdet.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.trend_sdet.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class FavoritesScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToFavorites() {
        composeTestRule.onNodeWithTag("bottom_nav_favorites").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun favoritesScreenShowsEmptyState() {
        navigateToFavorites()

        composeTestRule.onNodeWithTag("favorites_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
        composeTestRule.onNodeWithText("No favorites yet", substring = true).assertIsDisplayed()
    }

    @Test
    fun favoritesScreenShowsTitle() {
        navigateToFavorites()

        composeTestRule.onNodeWithTag("favorites_screen").assertIsDisplayed()
    }
}
