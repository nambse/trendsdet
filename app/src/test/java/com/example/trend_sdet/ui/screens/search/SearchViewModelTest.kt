package com.example.trend_sdet.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.trend_sdet.factory.TestProductFactory
import com.example.trend_sdet.fake.FakeFavoriteRepository
import com.example.trend_sdet.fake.FakeProductRepository
import com.example.trend_sdet.domain.model.PriceFilter
import com.example.trend_sdet.domain.model.PriceRange
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption
import com.example.trend_sdet.rule.MainDispatcherRule
import com.example.trend_sdet.util.UiState
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var productRepo: FakeProductRepository
    private lateinit var favoriteRepo: FakeFavoriteRepository

    @Before
    fun setup() {
        productRepo = FakeProductRepository()
        favoriteRepo = FakeFavoriteRepository()
    }

    private fun createViewModel(): SearchViewModel {
        return SearchViewModel(productRepo, favoriteRepo, SavedStateHandle())
    }

    @Test
    fun `initial searchQuery is empty`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.searchQuery.value).isEmpty()
    }

    @Test
    fun `updateQuery updates searchQuery state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("shoes")

        assertThat(viewModel.searchQuery.value).isEqualTo("shoes")
    }

    @Test
    fun `updateQuery with blank clears rawUiState immediately`() = runTest {
        productRepo.searchResult = Result.success(listOf(TestProductFactory.product()))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("shoes")
        advanceTimeBy(400)
        advanceUntilIdle()

        // Now clear
        viewModel.updateQuery("")
        advanceUntilIdle()

        // uiState should be back to empty success via the combine flow
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(UiState.Success::class.java)
            assertThat((state as UiState.Success).data).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search debounces by 300ms`() = runTest {
        productRepo.searchResult = Result.success(listOf(TestProductFactory.product()))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("shoes")
        advanceTimeBy(200)
        // Don't advanceUntilIdle here — that advances all scheduled work including the debounce

        assertThat(productRepo.lastSearchQuery).isNull()

        advanceTimeBy(150)
        advanceUntilIdle()

        assertThat(productRepo.lastSearchQuery).isEqualTo("shoes")
    }

    @Test
    fun `rapid query changes only trigger last search`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("a")
        advanceTimeBy(100)
        viewModel.updateQuery("ab")
        advanceTimeBy(100)
        viewModel.updateQuery("abc")
        advanceTimeBy(400)
        advanceUntilIdle()

        assertThat(productRepo.lastSearchQuery).isEqualTo("abc")
    }

    @Test
    fun `search success updates uiState`() = runTest {
        val products = listOf(TestProductFactory.product())
        productRepo.searchResult = Result.success(products)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            // Initial empty state
            assertThat(awaitItem()).isEqualTo(UiState.Success(emptyList<Product>()))

            viewModel.updateQuery("test")
            advanceTimeBy(400)
            advanceUntilIdle()

            // Skip Loading if present
            var state = awaitItem()
            if (state is UiState.Loading) state = awaitItem()
            assertThat(state).isEqualTo(UiState.Success(products))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search failure updates uiState to Error`() = runTest {
        productRepo.searchResult = Result.failure(Exception("Search failed"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            // Initial empty state
            assertThat(awaitItem()).isEqualTo(UiState.Success(emptyList<Product>()))

            viewModel.updateQuery("test")
            advanceTimeBy(400)
            advanceUntilIdle()

            // Skip Loading if present
            var state = awaitItem()
            if (state is UiState.Loading) state = awaitItem()
            assertThat(state).isInstanceOf(UiState.Error::class.java)
            assertThat((state as UiState.Error).message).isEqualTo("Search failed")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `price filter applied to successful results`() = runTest {
        val products = listOf(
            TestProductFactory.product(id = "p1", priceRange = PriceRange(TestProductFactory.money("10.00"), TestProductFactory.money("10.00"))),
            TestProductFactory.product(id = "p2", priceRange = PriceRange(TestProductFactory.money("30.00"), TestProductFactory.money("30.00"))),
            TestProductFactory.product(id = "p3", priceRange = PriceRange(TestProductFactory.money("50.00"), TestProductFactory.money("50.00"))),
        )
        productRepo.searchResult = Result.success(products)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            // Initial empty state
            assertThat(awaitItem()).isEqualTo(UiState.Success(emptyList<Product>()))

            viewModel.updateQuery("test")
            advanceTimeBy(400)
            advanceUntilIdle()

            // Skip Loading if present, then expect all 3 products
            var state = awaitItem()
            if (state is UiState.Loading) state = awaitItem()
            assertThat((state as UiState.Success).data).hasSize(3)

            viewModel.updatePriceFilter(PriceFilter(minPrice = 20.0, maxPrice = 40.0))
            advanceUntilIdle()

            val filtered = awaitItem() as UiState.Success
            assertThat(filtered.data).hasSize(1)
            assertThat(filtered.data.first().id).isEqualTo("p2")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `price filter not applied to error state`() = runTest {
        productRepo.searchResult = Result.failure(Exception("Error"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            // Initial empty state
            assertThat(awaitItem()).isEqualTo(UiState.Success(emptyList<Product>()))

            viewModel.updateQuery("test")
            advanceTimeBy(400)
            advanceUntilIdle()

            // Skip Loading if present
            var state = awaitItem()
            if (state is UiState.Loading) state = awaitItem()
            assertThat(state).isInstanceOf(UiState.Error::class.java)

            viewModel.updatePriceFilter(PriceFilter(minPrice = 20.0, maxPrice = 40.0))
            advanceUntilIdle()

            // Error state should persist, not affected by price filter
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectCollection sets selected and updates query`() = runTest {
        val collection = TestProductFactory.collection(title = "Summer")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectCollection(collection)

        assertThat(viewModel.selectedCollection.value).isEqualTo(collection)
        assertThat(viewModel.searchQuery.value).isEqualTo("Summer")
    }

    @Test
    fun `selectCollection with null clears selection`() = runTest {
        val collection = TestProductFactory.collection()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectCollection(collection)
        viewModel.selectCollection(null)

        assertThat(viewModel.selectedCollection.value).isNull()
    }

    @Test
    fun `updateSortOption triggers new search`() = runTest {
        productRepo.searchResult = Result.success(emptyList())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("shoes")
        advanceTimeBy(400)
        advanceUntilIdle()

        viewModel.updateSortOption(SortOption.PRICE_LOW_TO_HIGH)
        advanceUntilIdle()

        assertThat(productRepo.lastSearchSortKey).isEqualTo(SortOption.PRICE_LOW_TO_HIGH)
    }

    @Test
    fun `updateSortOption with blank query does not search`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateSortOption(SortOption.PRICE_LOW_TO_HIGH)
        advanceUntilIdle()

        assertThat(productRepo.lastSearchQuery).isNull()
    }

    @Test
    fun `toggleFilterSheet toggles visibility`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.showFilterSheet.value).isFalse()
        viewModel.toggleFilterSheet()
        assertThat(viewModel.showFilterSheet.value).isTrue()
        viewModel.toggleFilterSheet()
        assertThat(viewModel.showFilterSheet.value).isFalse()
    }

    @Test
    fun `dismissFilterSheet sets false`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFilterSheet()
        assertThat(viewModel.showFilterSheet.value).isTrue()
        viewModel.dismissFilterSheet()
        assertThat(viewModel.showFilterSheet.value).isFalse()
    }

    @Test
    fun `retry re-executes current query`() = runTest {
        productRepo.searchResult = Result.failure(Exception("Error"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateQuery("shoes")
        advanceTimeBy(400)
        advanceUntilIdle()

        productRepo.searchResult = Result.success(listOf(TestProductFactory.product()))
        viewModel.retry()
        advanceUntilIdle()

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(UiState.Success::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
