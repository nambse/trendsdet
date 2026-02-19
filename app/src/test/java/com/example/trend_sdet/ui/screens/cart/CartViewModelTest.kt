package com.example.trend_sdet.ui.screens.cart

import com.example.trend_sdet.factory.TestCartFactory
import com.example.trend_sdet.fake.FakeCartRepository
import com.example.trend_sdet.rule.MainDispatcherRule
import com.example.trend_sdet.util.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var cartRepo: FakeCartRepository

    @Before
    fun setup() {
        cartRepo = FakeCartRepository()
    }

    private fun createViewModel(): CartViewModel {
        return CartViewModel(cartRepo)
    }

    @Test
    fun `init loads cart and sets Success state`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)

        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(cart))
    }

    @Test
    fun `init with getCart failure sets Error state`() = runTest {
        cartRepo.getCartResult = Result.failure(Exception("Network error"))

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Error::class.java)
        assertThat((state as UiState.Error).message).isEqualTo("Network error")
    }

    @Test
    fun `loadCart does not set Loading if already Success`() = runTest {
        val cart = TestCartFactory.cart()
        cartRepo.getCartResult = Result.success(cart)
        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Success::class.java)

        val newCart = TestCartFactory.cart(id = "cart-2")
        cartRepo.getCartResult = Result.success(newCart)
        viewModel.loadCart()

        // Should go directly to Success without flashing Loading
        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(newCart))
    }

    @Test
    fun `loadCart sets Loading when state is Error`() = runTest {
        cartRepo.getCartResult = Result.failure(Exception("Error"))
        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Error::class.java)

        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        viewModel.loadCart()

        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Success::class.java)
    }

    @Test
    fun `updateQuantity success updates cart state`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        val updatedCart = TestCartFactory.cart(totalQuantity = 3)
        cartRepo.updateCartLineResult = Result.success(updatedCart)

        viewModel.updateQuantity("line-1", 3)

        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(updatedCart))
    }

    @Test
    fun `updateQuantity failure sets Error state`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        cartRepo.updateCartLineResult = Result.failure(Exception("Update failed"))
        viewModel.updateQuantity("line-1", 3)

        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun `updateQuantity passes correct arguments`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        cartRepo.updateCartLineResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        viewModel.updateQuantity("line-42", 5)

        assertThat(cartRepo.lastUpdateLineId).isEqualTo("line-42")
        assertThat(cartRepo.lastUpdateQuantity).isEqualTo(5)
    }

    @Test
    fun `removeItem success updates cart state`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        val emptyCart = TestCartFactory.emptyCart()
        cartRepo.removeCartLineResult = Result.success(emptyCart)

        viewModel.removeItem("line-1")

        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(emptyCart))
    }

    @Test
    fun `removeItem failure sets Error state`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        cartRepo.removeCartLineResult = Result.failure(Exception("Remove failed"))
        viewModel.removeItem("line-1")

        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Error::class.java)
    }

    @Test
    fun `removeItem passes correct lineId`() = runTest {
        cartRepo.getCartResult = Result.success(TestCartFactory.cart())
        cartRepo.removeCartLineResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()

        viewModel.removeItem("line-99")

        assertThat(cartRepo.lastRemoveLineId).isEqualTo("line-99")
    }
}
