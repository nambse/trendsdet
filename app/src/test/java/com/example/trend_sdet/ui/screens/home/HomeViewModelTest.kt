package com.example.trend_sdet.ui.screens.home

import app.cash.turbine.test
import com.example.trend_sdet.factory.TestProductFactory
import com.example.trend_sdet.fake.FakeFavoriteRepository
import com.example.trend_sdet.fake.FakeProductRepository
import com.example.trend_sdet.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepo: FakeProductRepository
    private lateinit var favoriteRepo: FakeFavoriteRepository

    @Before
    fun setup() {
        productRepo = FakeProductRepository()
        favoriteRepo = FakeFavoriteRepository()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(productRepo, favoriteRepo)
    }

    @Test
    fun `loadData success sets products and collections`() = runTest {
        val products = listOf(TestProductFactory.product())
        val collections = listOf(TestProductFactory.collection())
        productRepo.productsResult = Result.success(products)
        productRepo.collectionsResult = Result.success(collections)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.products).isEqualTo(products)
        assertThat(state.collections).isEqualTo(collections)
    }

    @Test
    fun `loadData sets error when both repositories fail`() = runTest {
        productRepo.productsResult = Result.failure(Exception("Network error"))
        productRepo.collectionsResult = Result.failure(Exception("Network error"))

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo("Network error")
    }

    @Test
    fun `loadData shows partial data when only products fail`() = runTest {
        val collections = listOf(TestProductFactory.collection())
        productRepo.productsResult = Result.failure(Exception("Products error"))
        productRepo.collectionsResult = Result.success(collections)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.products).isEmpty()
        assertThat(state.collections).isEqualTo(collections)
    }

    @Test
    fun `loadData shows partial data when only collections fail`() = runTest {
        val products = listOf(TestProductFactory.product())
        productRepo.productsResult = Result.success(products)
        productRepo.collectionsResult = Result.failure(Exception("Collections error"))

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.products).isEqualTo(products)
        assertThat(state.collections).isEmpty()
    }

    @Test
    fun `loadData calls both repositories`() = runTest {
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())

        createViewModel()

        assertThat(productRepo.getProductsCallCount).isEqualTo(1)
        assertThat(productRepo.getCollectionsCallCount).isEqualTo(1)
    }

    @Test
    fun `loadData with empty results returns success with empty lists`() = runTest {
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.products).isEmpty()
        assertThat(state.collections).isEmpty()
    }

    @Test
    fun `refresh updates data on success`() = runTest {
        productRepo.productsResult = Result.success(listOf(TestProductFactory.product()))
        productRepo.collectionsResult = Result.success(listOf(TestProductFactory.collection()))
        val viewModel = createViewModel()

        val newProducts = listOf(TestProductFactory.product(id = "new-1", title = "New"))
        productRepo.productsResult = Result.success(newProducts)
        viewModel.refresh()

        assertThat(viewModel.uiState.value.products).isEqualTo(newProducts)
    }

    @Test
    fun `refresh preserves existing data on failure`() = runTest {
        val products = listOf(TestProductFactory.product())
        val collections = listOf(TestProductFactory.collection())
        productRepo.productsResult = Result.success(products)
        productRepo.collectionsResult = Result.success(collections)
        val viewModel = createViewModel()

        productRepo.productsResult = Result.failure(Exception("Refresh failed"))
        productRepo.collectionsResult = Result.failure(Exception("Refresh failed"))
        viewModel.refresh()

        val state = viewModel.uiState.value
        assertThat(state.products).isEqualTo(products)
        assertThat(state.collections).isEqualTo(collections)
    }

    @Test
    fun `refresh sets isRefreshing to true then false`() = runTest {
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())
        val viewModel = createViewModel()

        viewModel.isRefreshing.test {
            assertThat(awaitItem()).isFalse()
            viewModel.refresh()
            assertThat(awaitItem()).isTrue()
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `toggleFavorite delegates to repository`() = runTest {
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())
        val viewModel = createViewModel()
        val product = TestProductFactory.product()

        viewModel.toggleFavorite(product)

        assertThat(favoriteRepo.toggleFavoriteCallCount).isEqualTo(1)
        assertThat(favoriteRepo.lastToggledProduct).isEqualTo(product)
    }

    @Test
    fun `favoriteIds reflects repository state`() = runTest {
        val product = TestProductFactory.product(id = "fav-1")
        favoriteRepo.setFavorites(listOf(product))
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())

        val viewModel = createViewModel()

        viewModel.favoriteIds.test {
            val ids = awaitItem()
            if (ids.isEmpty()) {
                assertThat(awaitItem()).contains("fav-1")
            } else {
                assertThat(ids).contains("fav-1")
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favoriteIds updates when favorite is toggled`() = runTest {
        productRepo.productsResult = Result.success(emptyList())
        productRepo.collectionsResult = Result.success(emptyList())
        val viewModel = createViewModel()
        val product = TestProductFactory.product(id = "toggle-1")

        viewModel.favoriteIds.test {
            awaitItem() // initial empty set
            viewModel.toggleFavorite(product)
            val updated = awaitItem()
            assertThat(updated).contains("toggle-1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error message is extracted from exception`() = runTest {
        productRepo.productsResult = Result.failure(java.io.IOException("Network timeout"))
        productRepo.collectionsResult = Result.failure(java.io.IOException("Network timeout"))

        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value.error).isEqualTo("Network timeout")
    }

    @Test
    fun `error uses fallback message when exception has null message`() = runTest {
        productRepo.productsResult = Result.failure(Exception())
        productRepo.collectionsResult = Result.failure(Exception())

        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value.error).isEqualTo("Failed to load")
    }
}
