package com.example.trend_sdet.ui.screens.favorites

import app.cash.turbine.test
import com.example.trend_sdet.factory.TestProductFactory
import com.example.trend_sdet.fake.FakeFavoriteRepository
import com.example.trend_sdet.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var favoriteRepo: FakeFavoriteRepository

    @Before
    fun setup() {
        favoriteRepo = FakeFavoriteRepository()
    }

    private fun createViewModel(): FavoritesViewModel {
        return FavoritesViewModel(favoriteRepo)
    }

    @Test
    fun `favorites initially emits empty list`() = runTest {
        val viewModel = createViewModel()

        assertThat(viewModel.favorites.value).isEmpty()
    }

    @Test
    fun `favorites reflects repository favorites`() = runTest {
        val products = listOf(
            TestProductFactory.product(id = "p1"),
            TestProductFactory.product(id = "p2"),
        )
        favoriteRepo.setFavorites(products)

        val viewModel = createViewModel()

        viewModel.favorites.test {
            val value = awaitItem()
            if (value.isEmpty()) {
                assertThat(awaitItem()).isEqualTo(products)
            } else {
                assertThat(value).isEqualTo(products)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favoriteIds reflects repository state`() = runTest {
        val products = listOf(TestProductFactory.product(id = "fav-1"))
        favoriteRepo.setFavorites(products)

        val viewModel = createViewModel()

        viewModel.favoriteIds.test {
            val ids = awaitItem()
            if (ids.isEmpty()) {
                assertThat(awaitItem()).containsExactly("fav-1")
            } else {
                assertThat(ids).containsExactly("fav-1")
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeFavorite delegates to repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.removeFavorite("product-1")

        assertThat(favoriteRepo.removeFavoriteCallCount).isEqualTo(1)
    }

    @Test
    fun `removeFavorite updates favorites flow`() = runTest {
        val products = listOf(
            TestProductFactory.product(id = "p1"),
            TestProductFactory.product(id = "p2"),
        )
        favoriteRepo.setFavorites(products)
        val viewModel = createViewModel()

        viewModel.favorites.test {
            // Get initial state
            var current = awaitItem()
            if (current.isEmpty()) current = awaitItem()
            assertThat(current).hasSize(2)

            viewModel.removeFavorite("p1")
            val updated = awaitItem()
            assertThat(updated).hasSize(1)
            assertThat(updated.first().id).isEqualTo("p2")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favorites emits updated list when repository changes`() = runTest {
        val viewModel = createViewModel()

        viewModel.favorites.test {
            assertThat(awaitItem()).isEmpty()

            val newProducts = listOf(TestProductFactory.product(id = "new-1"))
            favoriteRepo.setFavorites(newProducts)
            assertThat(awaitItem()).isEqualTo(newProducts)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple removals update state correctly`() = runTest {
        val products = listOf(
            TestProductFactory.product(id = "p1"),
            TestProductFactory.product(id = "p2"),
            TestProductFactory.product(id = "p3"),
        )
        favoriteRepo.setFavorites(products)
        val viewModel = createViewModel()

        viewModel.favorites.test {
            var current = awaitItem()
            if (current.isEmpty()) current = awaitItem()
            assertThat(current).hasSize(3)

            viewModel.removeFavorite("p1")
            assertThat(awaitItem()).hasSize(2)

            viewModel.removeFavorite("p3")
            val final = awaitItem()
            assertThat(final).hasSize(1)
            assertThat(final.first().id).isEqualTo("p2")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
