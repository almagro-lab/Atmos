# Atmos

An Android weather app that fetches real-time weather for random locations worldwide and persists a browsable history locally. Built with Clean Architecture across five Gradle modules.

## Architecture

```
:app ──▶ :presentation ──▶ :domain
 │                             ▲
 ├──▶ :data ───────────────────┤
 │      └──▶ :core
 └──▶ :core
```

| Module | Responsibility |
|--------|---------------|
| **`:app`** | Single-activity host. Owns the Compose theme, navigation graph, and Hilt application setup. |
| **`:presentation`** | ViewModels, UI state modeling, Compose screens, and domain-to-display mapping (`WeatherUi`). Depends only on `:domain`. |
| **`:domain`** | Pure Kotlin JVM library. Defines `Weather` model, repository interfaces, use cases, and domain errors. Zero Android dependencies. |
| **`:data`** | Retrofit API client, Room database, DTOs, mappers, and repository implementations. Depends on `:domain` and `:core`. |
| **`:core`** | Hilt-provided coroutine dispatchers (`@IoDispatcher`, `@DefaultDispatcher`, `@MainDispatcher`) shared across modules. No dependencies. |

`:domain` has no dependency on any other module. `:data` and `:presentation` both depend on `:domain` but never on each other — the Dependency Inversion Principle enforced at Gradle module boundaries.

## Tech Stack

| Category | Libraries |
|----------|-----------|
| Language | Kotlin 2.3.20 |
| UI | Jetpack Compose (BOM 2026.03.00), Material Design 3 |
| Navigation | Navigation Compose 2.9.0 with type-safe `@Serializable` route objects |
| Networking | Retrofit 3 + OkHttp 5 + kotlinx.serialization |
| Persistence | Room 2.7 with KSP annotation processing |
| Image loading | Coil 3 (Compose + OkHttp network backend) |
| DI | Hilt 2.59.2 (Dagger under the hood) |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit Jupiter 6, MockK 1.14.9, kotlinx-coroutines-test |
| Build | AGP 9.0.1, Gradle version catalog, custom convention plugins via `build-logic` |

## Key Engineering Decisions

### Modules over packages

Each architectural layer is a separate Gradle module, not just a package. This gives the build system the power to enforce dependency rules at compile time -- `:presentation` physically cannot import anything from `:data`. It also enables parallel compilation and independent build caching per module.

### UI model pattern -- `WeatherUi` and `toDisplayData()`

The domain `Weather` model carries raw values (temperature as `Double`, timezone as offset seconds). The presentation layer maps these to pre-formatted display strings via the `Weather.toDisplayData()` extension function, producing a `WeatherUi` where every field is a ready-to-render `String`. This keeps formatting logic testable in plain unit tests and keeps composables stateless renderers with no logic of their own.

### Custom Gradle convention plugins (`build-logic`)

Repetitive build configuration is extracted into two convention plugins:

- **`hilt.convention.plugin`** -- applies the KSP and Hilt plugins and adds the `hilt-android` / `hilt-compiler` dependencies. Every module that needs DI applies a single plugin ID instead of duplicating configuration.
- **`unit.test.convention.plugin`** -- configures JUnit Platform, adds JUnit Jupiter, MockK, and coroutines-test. Test infrastructure is identical across all modules with zero copy-paste.

A `Modules` object centralizes module path constants (`:core`, `:domain`, `:data`, `:presentation`) so module references are refactor-safe.

### Type-safe Navigation Compose routes

Navigation routes are `@Serializable data object` classes (`WeatherRoute`, `HistoryRoute`). The Navigation Compose library uses these types directly in `composable<WeatherRoute>` blocks, eliminating string-based route matching and providing compile-time safety.

### Room for local weather history

Successfully fetched weather is persisted to a Room database via `SaveWeatherUseCase`. The `WeatherDao.getHistory()` method returns `Flow<List<WeatherEntity>>`, which means the history screen receives live updates reactively. The `HistoryViewModel` exposes this as a `StateFlow` using `stateIn()` with `WhileSubscribed(5_000)` to keep the upstream alive for 5 seconds across configuration changes.

### Dispatcher injection

Coroutine dispatchers are provided through Hilt qualifiers (`@IoDispatcher`, `@DefaultDispatcher`, `@MainDispatcher`) defined in `:core`. The repository implementation injects `@IoDispatcher` for network and database calls. This makes dispatchers swappable in tests -- `WeatherRepositoryImplTest` injects `UnconfinedTestDispatcher` directly.

### Sealed interface for UI state

`WeatherUiState` is a `sealed interface` with `Loading`, `Success`, and `Error` variants. This forces exhaustive handling in the `when` expression inside the composable, so adding a new state variant produces a compile error until all screens handle it.

### Separate repository interfaces — `WeatherRemoteRepository` and `WeatherLocalRepository`

The original `WeatherRepository` combined three unrelated methods. Each use case only needs one of the two concerns — `FetchRandomWeatherUseCase` only fetches remotely, `SaveWeatherUseCase` and `GetWeatherHistoryUseCase` only touch local storage. Splitting the interface makes each use case's dependency surface explicit and enforces Interface Segregation at the domain boundary. `WeatherRepositoryImpl` implements both interfaces in a single class for now, with a clean path to splitting the implementation if either side grows independently.

### Typed domain errors — `WeatherException`

API failures are mapped to a `sealed class WeatherException` hierarchy (`LocationNotFound`, `Unauthorized`, `RateLimitExceeded`, `ServiceUnavailable`, `Network`) in `:domain` with zero Android or Retrofit imports. The `HttpExceptionMapper` in `:data` translates `retrofit2.HttpException` status codes before they leave the data layer, so use cases and the UI receive meaningful domain errors rather than raw HTTP exceptions.

## Project Structure

```
atmos/
  app/
    src/main/java/com/almagro/atmos/
      MainActivity.kt              # Single-activity Hilt entry point
      AtmosNavGraph.kt             # NavHost with type-safe routes
      NavRoutes.kt                 # @Serializable route objects
      ui/theme/                    # Material 3 color scheme and typography

  presentation/
    src/main/java/com/almagro/presentation/
      model/WeatherUi.kt           # UI model + toDisplayData() mapper
      state/WeatherUiState.kt      # Sealed interface: Loading | Success | Error
      viewModel/WeatherViewModel.kt
      viewModel/HistoryViewModel.kt
      ui/WeatherScreen.kt          # Main weather display
      ui/HistoryScreen.kt          # Recent locations list
    src/test/
      WeatherUiTest.kt
      WeatherViewModelTest.kt
      HistoryViewModelTest.kt

  domain/
    src/main/java/com/almagro/domain/
      model/Weather.kt
      error/WeatherException.kt    # Sealed exception hierarchy (LocationNotFound, Unauthorized, …)
      repository/WeatherRemoteRepository.kt  # fetchWeather(lat, lon)
      repository/WeatherLocalRepository.kt   # save(weather), getHistory()
      useCase/FetchRandomWeatherUseCase.kt
      useCase/SaveWeatherUseCase.kt
      useCase/GetWeatherHistoryUseCase.kt
      utils/LocationGenerator.kt
    src/test/
      FetchRandomWeatherUseCaseTest.kt
      LocationGeneratorTest.kt

  data/
    src/main/java/com/almagro/data/
      api/WeatherApi.kt            # Retrofit interface (OpenWeatherMap)
      model/WeatherDto.kt          # @Serializable DTOs
      model/WeatherEntity.kt       # Room entity
      error/HttpExceptionMapper.kt # HTTP status code → WeatherException
      mapper/WeatherMapper.kt      # DTO -> Domain
      mapper/WeatherEntityMapper.kt # Domain <-> Entity
      repository/WeatherRepositoryImpl.kt
      dao/WeatherDao.kt
      database/AtmosDatabase.kt
      di/DataModule.kt             # Hilt bindings for API, DB, repositories
    src/test/
      WeatherMapperTest.kt
      WeatherEntityMapperTest.kt
      WeatherRepositoryImplTest.kt
      error/HttpExceptionMapperTest.kt

  core/
    src/main/java/com/almagro/core/di/
      CoroutineDispatchers.kt      # @IoDispatcher, @DefaultDispatcher, @MainDispatcher qualifiers
      CoroutinesModule.kt          # Hilt provider for dispatchers

  build-logic/convention/
    src/main/java/
      plugins/android/HiltConventionPlugin.kt
      plugins/analysis/UnitTestConventionPlugin.kt
      extensions/                   # Version catalog helper extensions
      Modules.kt                   # Centralized module path constants
```

## Getting Started

### Prerequisites

- Android Studio Narwhal (2025.1+) or later
- JDK 17 (for the build-logic included build)
- An [OpenWeatherMap](https://openweathermap.org/api) API key (free tier works)

### Setup

```bash
git clone https://github.com/<your-username>/atmos.git
cd atmos
```

Create `local.properties` in the project root (this file is gitignored) and add your API key:

```properties
weather.api.key=YOUR_API_KEY_HERE
```

All modules use JUnit Jupiter via the shared `unit.test.convention.plugin`, so test configuration is consistent project-wide.

## Future Work

The following improvements were intentionally deferred given the scope of the challenge:

**Resilience**
- Retry logic in `FetchRandomWeatherUseCase` — retrying with a fresh location would significantly improve the success rate
- Exponential backoff on transient server errors — currently a single failed request surfaces immediately; there is no recovery strategy for 5xx responses beyond manually tapping retry
- Room migration strategy — currently there is no `fallbackToDestructiveMigration()` or schema migration, so any entity change will crash on upgrade

**UX**
- Fractional UTC offset display — timezones like India (UTC+5:30) are currently truncated to `UTC+5` due to integer division; correct handling requires formatting hours and minutes separately
- Offline support — the app always makes a live network request; caching the last successful response in Room would allow the current weather to be shown when offline
- Pull-to-refresh gesture — the only refresh mechanism is a FAB; a swipe-to-refresh gesture would match standard Android UX expectations
- Differentiated error UI per `WeatherException` variant — all errors currently render the same message string; a 401 (invalid API key) warrants different guidance than a network timeout

**Architecture**
- Split `WeatherRepositoryImpl` into `WeatherRemoteRepositoryImpl` and `WeatherLocalRepositoryImpl` — the domain layer already has separate interfaces; splitting the implementation would give each class a single responsibility and make each independently testable
- Inject `Random` into `LocationGenerator` — the current static `Random` usage cannot be seeded in tests, making location generation non-deterministic
- Proguard/R8 rules for Retrofit and kotlinx.serialization — `proguard-rules.pro` files are currently empty; a minified release build will strip serialization metadata and break API deserialization
- `BuildConfig.WEATHER_API_KEY` build-time validation — if `local.properties` is missing the key, the app compiles successfully but every API call returns 401 with no clear diagnostic at build time

**Testing**
- Tests for `SaveWeatherUseCase` and `GetWeatherHistoryUseCase` — thin delegators today but worth having as a specification baseline if logic is added later
- Integration test for the full data pipeline using `MockWebServer` — mapper and repository tests are independent; an end-to-end test against real JSON payloads would catch deserialization regressions
- Instrumented tests for the Room DAO — verifying the `ORDER BY id DESC LIMIT 10` query against a real in-memory database
- Compose UI tests — no `androidTest` coverage exists for `WeatherScreen` or `HistoryScreen`; at minimum each `WeatherUiState` variant should be verified to render the expected content

## AI Usage

[Claude Code](https://claude.ai/code) (Anthropic) was used as a coding assistant throughout this project.

Specifically, it was used for:
- **Code review** — correctness bugs and missing test coverage across the codebase
- **Boilerplate generation** — scaffolding convention plugins, Hilt modules and Room entities
- **Architectural guidance** — discussing trade-offs on decisions like the UI model pattern and module boundaries

All generated code was reviewed, understood, and intentionally accepted or modified. The architecture decisions, design choices, and overall structure reflect deliberate engineering judgement.
