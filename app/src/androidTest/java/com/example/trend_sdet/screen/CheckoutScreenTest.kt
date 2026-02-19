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
class CheckoutScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToCheckout() {
        composeTestRule.onNodeWithTag("bottom_nav_cart").performClick()
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Classic Sneakers"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("cart_checkout_button").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun checkoutScreenDisplaysOrderSummary() {
        navigateToCheckout()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Order Summary"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("checkout_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Order Summary").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checkout_place_order_button").assertIsDisplayed()
    }

    @Test
    fun placeOrderShowsSuccessScreen() {
        navigateToCheckout()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodes(hasText("Order Summary"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("checkout_place_order_button").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodes(hasText("Order Confirmed!"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Order Confirmed!").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checkout_order_number").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checkout_continue_shopping_button").assertIsDisplayed()
    }
}
