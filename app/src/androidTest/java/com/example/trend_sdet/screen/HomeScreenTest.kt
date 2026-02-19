package com.example.trend_sdet.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import com.example.trend_sdet.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenDisplaysProducts() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Classic Sneakers"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodes(hasText("Classic Sneakers")).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodes(hasText("Running Shoes")).onFirst().assertIsDisplayed()
    }

    @Test
    fun homeScreenDisplaysCollections() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("New Arrivals"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        // Collections may appear in both banner and category row
        composeTestRule.onAllNodes(hasText("New Arrivals")).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodes(hasText("Best Sellers")).onFirst().assertIsDisplayed()
    }

    @Test
    fun homeScreenShowsTitle() {
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_title").assertIsDisplayed()
    }
}
