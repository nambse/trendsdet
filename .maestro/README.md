# Maestro E2E Test Suite

End-to-end test suite for the Trend SDET Android app using [Maestro](https://maestro.mobile.dev/).

## Overview

Trend SDET is a Shopify-backed e-commerce app built with Jetpack Compose. The app has **90+ testTag identifiers** across 6 screens, enabling robust UI automation. This test suite validates all user-facing flows against the real Shopify Storefront API.

## Test Architecture

| Suite | Count | Purpose | Runtime |
|-------|-------|---------|---------|
| **Smoke** | 7 | Core sanity checks — app launches, navigation, basic UI | ~2 min |
| **Feature** | 12 | Full feature flows — search, cart, checkout, favorites | ~8 min |
| **Regression** | 6 | Edge cases — empty states, deep navigation, badge updates | ~4 min |
| **Subflows** | 8 | Reusable building blocks (not executed directly) | — |
| **Total** | 25 executable flows | | ~14 min |

### Smoke Tests
Quick sanity checks that validate the app is functional:

- `app_launches` — App starts, all navigation tabs visible
- `tab_navigation` — All 4 tabs navigate to correct screens
- `home_content_loads` — Banner, categories, and products load
- `search_field_works` — Search input produces results
- `cart_empty_state` — Empty cart shows correct UI
- `product_detail_opens` — PDP loads with title, price, add-to-cart
- `back_navigation` — Back button returns to previous screen

### Feature Tests
Complete feature validation flows:

- `home_banner_and_categories` — Banner carousel swipe, category tap, pull-to-refresh
- `search_text_and_results` — Search → results → clear → empty state cycle
- `search_filter_sort` — Filter sheet, sort options, price range slider
- `search_collection_chips` — Collection chip filtering
- `pdp_variant_selection` — Product title, price, stock indicator
- `pdp_add_to_cart` — Quantity adjustment, add to cart, badge update
- `pdp_favorites_and_share` — Favorite from PDP, verify in Favorites tab
- `pdp_description_toggle` — Description expand/collapse (conditional)
- `cart_manage_items` — Cart item list, subtotal, total, checkout button
- `cart_to_checkout` — Cart → checkout screen transition
- `checkout_full_order` — Complete order flow: cart → checkout → processing → success → home
- `favorites_add_remove` — Add/remove favorites lifecycle

### Regression Tests
Edge cases and complex navigation:

- `empty_cart_checkout` — No checkout button on empty cart, continue shopping works
- `search_no_results` — Nonexistent query shows "No products found", clear restores prompt
- `favorites_empty_state` — Empty favorites shows correct state, no grid visible
- `cart_badge_updates` — Badge appears after adding item, hidden when cart is empty
- `deep_navigation_stack` — Home → PDP → Back → Search → PDP → Back → Cart
- `pull_to_refresh_home` — Refresh gesture keeps content visible

## Selector Strategy

Tests use three selector approaches:

| Strategy | Example | When |
|----------|---------|------|
| `id:` (testTag) | `id: "home_screen"` | Static elements with known testTags |
| `text:` (regex) | `text: "\\$.*"` with `index: 0` | Dynamic content like prices |
| `text:` (contentDescription) | `text: "Add to favorites"` | Icon buttons with accessibility labels |

All testTags are exposed as resource IDs via `testTagsAsResourceId = true` in `MainActivity`.

## Running Locally

### Prerequisites
- Android emulator or device running
- Maestro CLI installed: `curl -Ls "https://get.maestro.mobile.dev" | bash`
- App installed on device: `./gradlew installDebug`

### Run all tests
```bash
maestro test .maestro/
```

### Run by suite
```bash
# Smoke only (~2 min)
maestro test .maestro/ --include-tags smoke

# Feature only (~8 min)
maestro test .maestro/ --include-tags feature

# Regression only (~4 min)
maestro test .maestro/ --include-tags regression
```

### Run a single flow
```bash
maestro test .maestro/smoke/app_launches.yaml
```

### Interactive debugging
```bash
maestro studio
```

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/maestro-tests.yml`) runs tests on a local Android emulator:

| Trigger | Tests Run | Duration |
|---------|-----------|----------|
| Pull request to `main` | Smoke only | ~5 min |
| Push to `main` | Smoke + Feature + Regression | ~20 min |

The pipeline:
1. Builds the debug APK with Gradle
2. Boots an Android emulator (API 33, x86_64)
3. Installs the APK and Maestro CLI
4. Runs the appropriate test suite
5. Uploads JUnit XML results as artifacts

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `SHOPIFY_DOMAIN` | Shopify store domain (e.g., `store.myshopify.com`) |
| `SHOPIFY_STOREFRONT_TOKEN` | Shopify Storefront API access token |

## Design Principles

- **Test independence** — Every flow starts with `clearState` + `launchApp` for full isolation
- **Real backend** — Tests run against the live Shopify Storefront API, no mocks
- **Generous timeouts** — Network calls use 15s timeouts, checkout processing uses 30s
- **Reusable subflows** — 8 shared subflows eliminate duplication across 25 tests
- **Progressive confidence** — Smoke → Feature → Regression builds confidence incrementally
- **No hardcoded IDs** — Shopify GIDs are dynamic; tests use text/index/contentDescription selectors
- **Conditional logic** — Features like description toggle use `when: visible` for products that may not have the element
