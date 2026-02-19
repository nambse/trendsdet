package com.example.trend_sdet.ui.screens.checkout

import app.cash.turbine.test
import com.example.trend_sdet.factory.TestCartFactory
import com.example.trend_sdet.factory.TestProductFactory
import com.example.trend_sdet.fake.FakeCartRepository
import com.example.trend_sdet.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var cartRepo: FakeCartRepository

    @Before
    fun setup() {
        cartRepo = FakeCartRepository()
    }

    private fun createViewModel(): CheckoutViewModel {
        return CheckoutViewModel(cartRepo)
    }

    @Test
    fun `init loads cart summary on success`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(CheckoutUiState.Summary(cart))
    }

    @Test
    fun `init with empty cart sets Error`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.emptyCart())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CheckoutUiState.Error::class.java)
        assertThat((state as CheckoutUiState.Error).message).isEqualTo("Cart is empty")
    }

    @Test
    fun `init with null cart sets Error`() = runTest {
        cartRepo.getCartResult = Result.success(null)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CheckoutUiState.Error::class.java)
        assertThat((state as CheckoutUiState.Error).message).isEqualTo("Cart is empty")
    }

    @Test
    fun `init with getCart failure sets Error`() = runTest {
        cartRepo.getCartResult = Result.failure(Exception("Network failure"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(CheckoutUiState.Error::class.java)
        assertThat((state as CheckoutUiState.Error).message).isEqualTo("Network failure")
    }

    @Test
    fun `placeOrder transitions Summary to Processing to Success`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.placeOrder()
        advanceTimeBy(100)

        assertThat(viewModel.uiState.value).isEqualTo(CheckoutUiState.Processing)

        advanceTimeBy(2000)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(CheckoutUiState.Success::class.java)
    }

    @Test
    fun `placeOrder generates order number starting with TRD-`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.placeOrder()
        advanceTimeBy(2500)
        advanceUntilIdle()

        val state = viewModel.uiState.value as CheckoutUiState.Success
        assertThat(state.orderNumber).startsWith("TRD-")
        assertThat(state.orderNumber).hasLength(10) // TRD-123456
    }

    @Test
    fun `placeOrder sets totalFormatted from cart`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.placeOrder()
        advanceTimeBy(2500)
        advanceUntilIdle()

        val state = viewModel.uiState.value as CheckoutUiState.Success
        assertThat(state.totalFormatted).isEqualTo(cart.totalAmount.formatted)
    }

    @Test
    fun `placeOrder calls clearLocalCart on success`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.placeOrder()
        advanceTimeBy(2500)
        advanceUntilIdle()

        assertThat(cartRepo.clearLocalCartCalled).isTrue()
    }

    @Test
    fun `placeOrder when not in Summary state is no-op`() = runTest {
        cartRepo.getCartResult = Result.failure(Exception("Error"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.placeOrder()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(CheckoutUiState.Error::class.java)
    }

    @Test
    fun `placeOrder Processing state appears during 2s delay`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(CheckoutUiState.Summary::class.java)

            viewModel.placeOrder()
            assertThat(awaitItem()).isEqualTo(CheckoutUiState.Processing)

            advanceTimeBy(2100)
            assertThat(awaitItem()).isInstanceOf(CheckoutUiState.Success::class.java)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
