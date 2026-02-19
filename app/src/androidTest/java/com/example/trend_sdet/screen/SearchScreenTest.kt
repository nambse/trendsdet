package com.example.trend_sdet.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.trend_sdet.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToSearch() {
        composeTestRule.onNodeWithTag("bottom_nav_search").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun searchScreenDisplaysCollectionChips() {
        navigateToSearch()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("New Arrivals"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("New Arrivals").assertIsDisplayed()
        composeTestRule.onNodeWithText("Best Sellers").assertIsDisplayed()
    }

    @Test
    fun searchReturnsMatchingResults() {
        navigateToSearch()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("search_text_field").performTextInput("Sneakers")

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Classic Sneakers"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Classic Sneakers").assertIsDisplayed()
    }
}
