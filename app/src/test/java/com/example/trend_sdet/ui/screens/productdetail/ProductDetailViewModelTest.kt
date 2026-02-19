package com.example.trend_sdet.ui.screens.productdetail

import app.cash.turbine.test
import com.example.trend_sdet.factory.TestCartFactory
import com.example.trend_sdet.factory.TestProductFactory
import com.example.trend_sdet.fake.FakeCartRepository
import com.example.trend_sdet.fake.FakeFavoriteRepository
import com.example.trend_sdet.fake.FakeProductRepository
import com.example.trend_sdet.rule.MainDispatcherRule
import com.example.trend_sdet.util.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepo: FakeProductRepository
    private lateinit var cartRepo: FakeCartRepository
    private lateinit var favoriteRepo: FakeFavoriteRepository

    @Before
    fun setup() {
        productRepo = FakeProductRepository()
        cartRepo = FakeCartRepository()
        favoriteRepo = FakeFavoriteRepository()
    }

    private fun createViewModel(): ProductDetailViewModel {
        return ProductDetailViewModel(productRepo, cartRepo, favoriteRepo)
    }

    // --- Load Product ---

    @Test
    fun `loadProduct success sets UiState to Success`() = runTest {
        val product = TestProductFactory.product()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success(product))
    }

    @Test
    fun `loadProduct failure sets UiState to Error`() = runTest {
        productRepo.productByIdResult = Result.failure(Exception("Not found"))
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(UiState.Error::class.java)
        assertThat((state as UiState.Error).message).isEqualTo("Not found")
    }

    @Test
    fun `loadProduct selects first available variant`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        val selected = viewModel.selectedVariant.value
        assertThat(selected).isNotNull()
        assertThat(selected!!.availableForSale).isTrue()
        assertThat(selected.id).isEqualTo("v-sm-black")
    }

    @Test
    fun `loadProduct with all unavailable selects first variant`() = runTest {
        val variants = listOf(
            TestProductFactory.variant(id = "v1", availableForSale = false),
            TestProductFactory.variant(id = "v2", availableForSale = false),
        )
        val product = TestProductFactory.product(variants = variants)
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        assertThat(viewModel.selectedVariant.value?.id).isEqualTo("v1")
    }

    @Test
    fun `loadProduct with empty variants sets selectedVariant to null`() = runTest {
        val product = TestProductFactory.product(variants = emptyList())
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        assertThat(viewModel.selectedVariant.value).isNull()
    }

    // --- Option Groups ---

    @Test
    fun `deriveOptionGroups extracts unique names and values`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()

        viewModel.loadProduct("product-1")

        val groups = viewModel.optionGroups.value
        assertThat(groups).hasSize(2)
        assertThat(groups[0].name).isEqualTo("Size")
        assertThat(groups[0].values).containsExactly("S", "M", "L").inOrder()
        assertThat(groups[1].name).isEqualTo("Color")
        assertThat(groups[1].values).containsExactly("Black", "White").inOrder()
    }

    // --- Select Option ---

    @Test
    fun `selectOption updates selectedOptions map`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        viewModel.selectOption("Size", "L")

        assertThat(viewModel.selectedOptions.value["Size"]).isEqualTo("L")
    }

    @Test
    fun `selectOption finds matching variant`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        viewModel.selectOption("Size", "L")
        viewModel.selectOption("Color", "Black")

        assertThat(viewModel.selectedVariant.value?.id).isEqualTo("v-lg-black")
    }

    @Test
    fun `selectOption with no matching variant keeps previous`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        val beforeVariant = viewModel.selectedVariant.value
        viewModel.selectOption("Size", "L")
        viewModel.selectOption("Color", "White")

        // L/White doesn't exist in our test data, so variant should stay at previous
        // Since we changed to L/Black first (which exists), it should keep L/Black
        assertThat(viewModel.selectedVariant.value?.id).isEqualTo("v-lg-black")
    }

    @Test
    fun `selectOption when uiState is not Success is no-op`() = runTest {
        val viewModel = createViewModel()

        viewModel.selectOption("Size", "L")

        assertThat(viewModel.selectedOptions.value).isEmpty()
    }

    @Test
    fun `selectVariant directly sets variant and updates options`() = runTest {
        val product = TestProductFactory.productWithVariants()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        val lgBlack = product.variants[2] // L / Black
        viewModel.selectVariant(lgBlack)

        assertThat(viewModel.selectedVariant.value).isEqualTo(lgBlack)
        assertThat(viewModel.selectedOptions.value["Size"]).isEqualTo("L")
        assertThat(viewModel.selectedOptions.value["Color"]).isEqualTo("Black")
    }

    // --- Quantity ---

    @Test
    fun `increaseQuantity increments by 1`() = runTest {
        val viewModel = createViewModel()

        viewModel.increaseQuantity()

        assertThat(viewModel.quantity.value).isEqualTo(2)
    }

    @Test
    fun `increaseQuantity clamps at 10`() = runTest {
        val viewModel = createViewModel()

        repeat(15) { viewModel.increaseQuantity() }

        assertThat(viewModel.quantity.value).isEqualTo(10)
    }

    @Test
    fun `decreaseQuantity decrements by 1`() = runTest {
        val viewModel = createViewModel()
        viewModel.increaseQuantity() // now 2

        viewModel.decreaseQuantity()

        assertThat(viewModel.quantity.value).isEqualTo(1)
    }

    @Test
    fun `decreaseQuantity clamps at 1`() = runTest {
        val viewModel = createViewModel()

        viewModel.decreaseQuantity()

        assertThat(viewModel.quantity.value).isEqualTo(1)
    }

    // --- Add to Cart ---

    @Test
    fun `addToCart success transitions to Success and resets quantity`() = runTest {
        val product = TestProductFactory.product()
        productRepo.productByIdResult = Result.success(product)
        cartRepo.addToCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")
        viewModel.increaseQuantity() // qty = 2

        viewModel.addToCart()

        assertThat(viewModel.addToCartState.value).isEqualTo(AddToCartState.Success)
        assertThat(viewModel.quantity.value).isEqualTo(1) // reset
    }

    @Test
    fun `addToCart failure transitions to Error`() = runTest {
        val product = TestProductFactory.product()
        productRepo.productByIdResult = Result.success(product)
        cartRepo.addToCartResult = Result.failure(Exception("Cart error"))
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        viewModel.addToCart()

        val state = viewModel.addToCartState.value
        assertThat(state).isInstanceOf(AddToCartState.Error::class.java)
        assertThat((state as AddToCartState.Error).message).isEqualTo("Cart error")
    }

    @Test
    fun `addToCart with no selected variant is no-op`() = runTest {
        val viewModel = createViewModel()

        viewModel.addToCart()

        assertThat(viewModel.addToCartState.value).isEqualTo(AddToCartState.Idle)
    }

    @Test
    fun `addToCart passes correct variantId and quantity`() = runTest {
        val variant = TestProductFactory.variant(id = "my-variant")
        val product = TestProductFactory.product(variants = listOf(variant))
        productRepo.productByIdResult = Result.success(product)
        cartRepo.addToCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")
        viewModel.increaseQuantity()
        viewModel.increaseQuantity() // qty = 3

        viewModel.addToCart()

        assertThat(cartRepo.lastAddToCartVariantId).isEqualTo("my-variant")
        assertThat(cartRepo.lastAddToCartQuantity).isEqualTo(3)
    }

    @Test
    fun `resetAddToCartState returns to Idle`() = runTest {
        val product = TestProductFactory.product()
        productRepo.productByIdResult = Result.success(product)
        cartRepo.addToCartResult = Result.success(TestCartFactory.cart())
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")
        viewModel.addToCart()

        viewModel.resetAddToCartState()

        assertThat(viewModel.addToCartState.value).isEqualTo(AddToCartState.Idle)
    }

    // --- Favorites ---

    @Test
    fun `toggleFavorite when product not loaded is no-op`() = runTest {
        val viewModel = createViewModel()

        viewModel.toggleFavorite()

        assertThat(favoriteRepo.toggleFavoriteCallCount).isEqualTo(0)
    }

    @Test
    fun `toggleFavorite delegates to repository`() = runTest {
        val product = TestProductFactory.product()
        productRepo.productByIdResult = Result.success(product)
        val viewModel = createViewModel()
        viewModel.loadProduct("product-1")

        viewModel.toggleFavorite()

        assertThat(favoriteRepo.toggleFavoriteCallCount).isEqualTo(1)
    }

    @Test
    fun `isFavorite reflects repository state`() = runTest {
        val product = TestProductFactory.product(id = "fav-product")
        productRepo.productByIdResult = Result.success(product)
        favoriteRepo.setFavorites(listOf(product))
        val viewModel = createViewModel()
        viewModel.loadProduct("fav-product")

        viewModel.isFavorite.test {
            val value = awaitItem()
            if (!value) {
                assertThat(awaitItem()).isTrue()
            } else {
                assertThat(value).isTrue()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
