# Trend SDET

A Shopify-backed e-commerce Android app built with Jetpack Compose, designed to showcase QA engineering and automated testing capabilities.

## Tech Stack

- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with Hilt dependency injection
- **Backend:** Shopify Storefront API via Mobile Buy SDK
- **Navigation:** Compose Navigation with type-safe routes
- **Image Loading:** Coil 3
- **Local Storage:** Room (favorites persistence)
- **Build:** AGP 9.0.1 with built-in Kotlin 2.2.10, KSP

## Features

- **Home** — Banner carousel, category browsing, product grid
- **Search** — Text search, collection filtering, sort/price filter
- **Product Detail** — Image carousel, variant selection, quantity picker, add to cart
- **Cart** — Item management, quantity updates, checkout flow
- **Checkout** — Order summary, place order, success confirmation
- **Favorites** — Local favorites with heart toggle across all screens

## Building and Running

### Prerequisites
- Android Studio Meerkat or later
- JDK 17
- Android emulator or device (API 26+)

### Setup
1. Clone the repository
2. Set environment variables for Shopify credentials:
   ```bash
   export SHOPIFY_DOMAIN="your-store.myshopify.com"
   export SHOPIFY_STOREFRONT_TOKEN="your-token"
   ```
3. Build and run:
   ```bash
   ./gradlew installDebug
   ```

## Testing

The app includes **90+ testTag identifiers** across all 6 screens, with `testTagsAsResourceId = true` for Maestro and Espresso compatibility.

### Maestro E2E Tests

A comprehensive Maestro test suite with **25 flows** organized into smoke, feature, and regression suites.

```bash
# Install Maestro
curl -Ls "https://get.maestro.mobile.dev" | bash

# Run all tests
maestro test .maestro/

# Run smoke tests only
maestro test .maestro/ --include-tags smoke
```

See [`.maestro/README.md`](.maestro/README.md) for the full test strategy documentation.

### CI/CD

GitHub Actions runs Maestro tests automatically:
- **Pull requests:** Smoke tests for fast feedback
- **Push to main:** Full suite (smoke + feature + regression)

## Architecture

```
app/src/main/java/com/example/trend_sdet/
├── data/           # Repository implementations, Shopify mappers
├── di/             # Hilt modules
├── domain/model/   # Domain models (Product, Cart, Collection, etc.)
├── navigation/     # Routes and navigation graph
├── ui/
│   ├── components/ # Reusable Compose components
│   ├── screens/    # Screen composables + ViewModels
│   └── theme/      # Material 3 theming
└── util/           # UiState, extensions
```
