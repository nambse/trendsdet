package com.example.trend_sdet.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
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
class CartScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToCart() {
        composeTestRule.onNodeWithTag("bottom_nav_cart").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun cartScreenDisplaysCartItems() {
        navigateToCart()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Classic Sneakers"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Classic Sneakers").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cart_screen").assertIsDisplayed()
    }

    @Test
    fun cartScreenDisplaysTotalAmount() {
        navigateToCart()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Classic Sneakers"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("cart_total_amount").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cart_checkout_button").assertIsDisplayed()
    }
}
