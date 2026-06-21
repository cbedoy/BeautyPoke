# BeautyPoke — Project Conventions & Agent Guide

## 1. Project Overview

Android application following **MVVM + Clean Architecture + SOLID** principles.  
Uses **Retrofit** for REST API consumption, **Use Cases** for business logic, **Koin** for DI, **Jetpack Compose** for UI, **kotlinx-serialization-json** for structured data serialization, **kotlinx-datetime** for date/time handling, **ktlint** for code formatting, and **detekt** for static analysis.

## 2. Package Structure (Clean Architecture)

```
com.mx.beautypoke
├── data
│   ├── remote         # Retrofit services, DTOs, mappers
│   ├── local          # Room entities, DAOs, database, entity mappers
│   └── repository     # Repository implementations
├── domain
│   ├── model          # Domain entities (Pokemon, PokemonType, PokemonColor, TypeWeaknesses)
│   ├── repository     # Repository interfaces
│   └── usecase        # Business logic use cases (GetPokemonDetailUseCase)
├── di                 # Koin modules (NetworkModule, RepositoryModule, UseCaseModule, ViewModelModule)
└── presentation
    ├── navigation     # NavHost & routes
    ├── theme          # Compose theme + PokemonTheme (per-type color palettes)
    ├── component      # Reusable composables (PokemonTypeBadge, StatBar, WeaknessPill, InfoSection, MetricItem, AbstractPattern)
    ├── screen         # Screen composables (PokemonDetailScreen)
    └── viewmodel      # PokemonDetailViewModel
```

### Dependency rule
- `presentation` → `domain` → `data`  
- `domain` has **zero** dependencies on Android or `data` layer.

### Room caching strategy
- Repository implements **cache-first** logic: check Room → return cached entity, else fetch API → persist to Room → return.
- `PokemonEntity` stores complex fields (`types`, `stats`) as JSON strings using Gson.
- `PokemonColor` stored as enum `name` string, mapped back via `PokemonColor.valueOf()`.
- DAO uses `@Upsert` (insert or update) for clean cache writes.
- `BeautyPokeDatabase` provided via Koin `DatabaseModule`, requires `androidContext()` in `BeautyPokeApp`.

## 3. Architecture Rules

### MVVM
- `ViewModel` holds `StateFlow<UiState>` exposed to Compose.
- UI is unidirectional: screen emits events → ViewModel processes → new state emitted.
- No business logic inside Composables.

### Clean Architecture layers

| Layer | Responsibility | Dependencies |
|-------|----------------|--------------|
| `domain` | Entities, repository interfaces, use cases | None |
| `data` | API calls (Retrofit), DTO → domain mapping, Room caching | domain |
| `presentation` | Compose screens, ViewModels, navigation | domain |
| `di` | Wiring all layers with Koin | All layers |

### SOLID
- Single Responsibility: each class has one reason to change.
- Open/Closed: use sealed classes for UI states, Result wrappers for API outcomes.
- Liskov: repository interfaces defined in domain, implemented in data.
- Interface Segregation: small focused interfaces.
- Dependency Inversion: domain defines repository contracts, data implements them.

## 4. API — PokéAPI v2

- **Base URL:** `https://pokeapi.co/api/v2/`
- **No authentication required** — free and open, no rate limiting.
- **Endpoints used:**
  - `GET /pokemon/{id}` — id, name, types, stats, height, weight, sprites
  - `GET /pokemon-species/{id}` — color, genera (category), flavor text, evolution chain

## 5. Network Layer (Retrofit)

- API interfaces defined in `data/remote/api/`.
- DTOs in `data/remote/dto/` with `@SerializedName` for snake_case mapping.
- Retrofit instance provided via Koin `NetworkModule` in `di/`.
- DTO → Domain mapping via `PokemonMapper` in `data/remote/mapper/`.
- Use `runCatching { }` in repository for error handling → `Result<T>`.
- `OkHttpClient` with 15s connect/read timeouts.
- Gson for JSON deserialization (Retrofit + Room entity fields).
- `kotlinx-serialization-json` is also available as an alternative serialization library (e.g., for `@Serializable` domain models).
- `kotlinx-datetime` is available for date/time handling (e.g., cache freshness timestamps).

### Mapping rules
| Domain field | API source | Notes |
|---|---|---|
| `id` | `pokemon.id` | |
| `name` | `pokemon.name` | First letter capitalized |
| `imageUrl` | `sprites.other.official-artwork.front_default` | Falls back to PokeAPI raw URL |
| `types` | `types[].type.name` | Mapped to `PokemonType` enum |
| `stats` | `stats[].base_stat` + `stats[].stat.name` | English names: HP, Attack, Defense, Sp. Atk, Sp. Def, Speed |
| `height` | `pokemon.height` | Decimeters (convert to meters: `/ 10.0`) |
| `weight` | `pokemon.weight` | Hectograms (convert to kg: `/ 10.0`) |
| `description` | `species.flavor_text_entries` | Prefers English "sword" version; cleans `\f` characters |
| `color` | `species.color.name` | Mapped to `PokemonColor` enum |
| `category` | `species.genera` | Filters by `language.name == "en"`, e.g. "Flame Pokémon" |

## 6. Use Cases

Each use case is a single-purpose class with an `operator fun invoke(...)`:

```kotlin
class GetPokemonDetailUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int): Result<Pokemon> { ... }
}
```

## 7. ViewModel Conventions

- One ViewModel per screen or feature group.
- Expose `StateFlow<UiState<T>>` where `UiState` is a sealed interface with `Loading`, `Success`, `Error`.
- Use `viewModelScope` for coroutine lifecycle.
- ViewModel constructor receives use cases via Koin injection.

## 8. Compose UI

- Screens in `presentation/screen/`.
- Reusable components in `presentation/component/`.
- Theme (colors, typography, shapes) in `presentation/theme/`.
- `PokemonTheme` data class with per-type palettes in `presentation/theme/PokemonTheme.kt`. Use `PokemonType.toTheme()` to resolve.
- Navigation graph in `presentation/navigation/BeautyPokeNavHost.kt`.
- **State hoisting**: ViewModel owns the state (`StateFlow`), screen composables receive state + event callbacks as parameters. No `remember` or `mutableStateOf` for business data in composables.
- **UiState** is a `sealed interface` with `Loading`, `Success`, `Error` variants defined in `domain/model/`.
- **Domain models** (`domain/model/`) are pure Kotlin data classes with no Android dependencies.
- **Image loading**: Use Coil's `AsyncImage` for remote images.
- **Pokemon types**: `PokemonType` enum with `displayName` in English. Uses `resolveColor()` for legacy `PokemonColor` mapping.
- **Measurements**: Height in decimeters → meters (`/ 10.0`), weight in hectograms → kilograms (`/ 10.0`).

### PokemonDetailScreen layout (single-card redesign)

```
PokemonDetailScreen
├── TopSection (gradient + abstract pattern + type badges + image emerging from PokeBall)
├── CurvedNameTransition (name + national index)
├── InfoPanel (dark surface with themed palette)
│   ├── Description (Pokedex flavor text)
│   ├── Base Stats (StatBar rows with animated bars)
│   ├── Type & Category (circular type badges + genus text)
│   ├── Weaknesses (FlowRow of WeaknessPill badges from TypeWeaknesses lookup)
│   └── Weight & Height (MetricItem with icons)
└── BackButton (floating circular, AutoMirrored ArrowBack)
```

### Available components

| Component | File | Description |
|-----------|------|-------------|
| `PokemonTypeBadge` | `PokemonTypeBadge.kt` | Rounded or circular badge with type color. `PokemonTypeBadgeStyle.ROUNDED` / `CIRCULAR` |
| `StatBar` | `StatBar.kt` | Animated stat bar with label, value, and colored progress. Dark-panel compatible via `onSurfaceColor` |
| `WeaknessPill` | `WeaknessPill.kt` | Small pill with type dot + name for weakness display |
| `InfoSection` | `InfoSection.kt` | Section with uppercased title + content lambda |
| `MetricItem` | `MetricItem.kt` | Icon + value + label row for weight/height |
| `AbstractPattern` | `AbstractPattern.kt` | Type-themed decorative Canvas patterns (geometric, smoke, rings, dots) |

## 9. Dependency Injection (Koin)

Modules in `di/`:

- `NetworkModule` — Retrofit, OkHttp, Gson, ApiService
- `DatabaseModule` — Room database `BeautyPokeDatabase` and `PokemonDao`
- `RepositoryModule` — binds `PokemonRepository` to `PokemonRepositoryImpl`
- `UseCaseModule` — provides `GetPokemonDetailUseCase`
- `ViewModelModule` — provides `PokemonDetailViewModel`

`BeautyPokeApp` Application class initializes Koin with all modules. Must call `androidContext(this@BeautyPokeApp)` before `modules()` because `DatabaseModule` requires a `Context`.

## 10. Testing

- `domain/` — pure unit tests (JUnit + MockK or Turbine for flows).
- `data/` — unit tests with mocked Retrofit (MockWebServer).
- `presentation/` — ViewModel tests with fake use cases; Compose UI tests; Paparazzi snapshot tests.

### Test dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit 4 | 4.13.2 | Test runner & assertions |
| MockK | 1.13.14 | Mocking (coEvery, coVerify, mockk) |
| Turbine | 1.2.0 | StateFlow/Flow testing with `test {}` block |
| kotlinx-coroutines-test | 1.10.1 | `runTest`, `StandardTestDispatcher`, `advanceUntilIdle` |

### ViewModel test conventions (Turbine + StandardTestDispatcher)

```kotlin
class PokemonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test description`() = runTest(testDispatcher) {
        val useCase = mockk<GetPokemonRangeUseCase>()
        coEvery { useCase.invoke(any()) } returns Result.success(...)

        val viewModel = PokemonDetailViewModel(useCase)

        viewModel.uiState.test {
            // 1. Initial state is always the first emission
            assertEquals(PokemonCarouselUiState.Loading, awaitItem())

            // 2. Advance all pending coroutines (init block, launches)
            testDispatcher.scheduler.advanceUntilIdle()

            // 3. Assert the next emission after all work completes
            val success = awaitItem() as PokemonCarouselUiState.Success
            assertEquals(expected, success.pokemonList.size)
        }
    }
}
```

**Key rules:**
- Use `StandardTestDispatcher` (not `UnconfinedTestDispatcher`) — enables explicit control with `advanceUntilIdle()`.
- Call `Dispatchers.setMain(testDispatcher)` in `@Before` so `viewModelScope.launch` uses the test dispatcher.
- Use `testDispatcher.scheduler.advanceUntilIdle()` inside the turbine `test {}` block after each state mutation.
- Use `cancelAndIgnoreRemainingEvents()` at the end of the `test {}` block if prefetch coroutines produce extra emissions.
- Import `com.mx.beautypoke.domain.model.*` for domain types in assertions.
- Use `coEvery { ... }` for suspend function mocking, `coVerify` for verification.

### Repository test conventions (MockK + cache-first)

```kotlin
class PokemonRepositoryImplTest {

    private val api = mockk<PokemonApiService>()
    private val dao = mockk<PokemonDao>()
    private val repository = PokemonRepositoryImpl(api, dao)

    @Test
    fun `cache hit returns cached entity`() = runTest {
        coEvery { dao.getById(1) } returns cachedEntity

        val result = repository.getPokemonDetail(1)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { api.getPokemon(any()) }
        coVerify(exactly = 0) { api.getPokemonSpecies(any()) }
    }

    @Test
    fun `cache miss fetches and persists`() = runTest {
        coEvery { dao.getById(1) } returns null
        coEvery { api.getPokemon(1) } returns pokemonResponse
        coEvery { api.getPokemonSpecies(1) } returns speciesResponse
        coEvery { dao.upsert(any()) } returns Unit

        val result = repository.getPokemonDetail(1)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { dao.upsert(any()) }
    }
}
```

### Entity mapper test conventions

```kotlin
class PokemonEntityMapperTest {

    private val mapper = PokemonEntityMapper

    @Test
    fun `roundtrip preserves all data`() {
        val entity = mapper.toEntity(original)
        val result = mapper.toDomain(entity)
        assertEquals(original, result)
    }
}
```

### Compose UI test conventions (createComposeRule + MockK)

```kotlin
class PokemonDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysPokemonName() {
        val mock = mockk<PokemonDetailViewModel>(relaxed = true)
        val stateFlow = MutableStateFlow(
            PokemonCarouselUiState.Success(listOf(pokemon), 0)
        ).asStateFlow()
        every { mock.uiState } returns stateFlow

        composeTestRule.setContent {
            PokemonDetailScreen(viewModel = mock, onBackClick = {})
        }

        composeTestRule.onNodeWithText("Bulbasaur").assertExists()
    }
}
```

**Key rules:**
- Use `createComposeRule()` as `@get:Rule` for Compose test environment.
- Mock the ViewModel with `mockk<ViewModel>(relaxed = true)` and stub `uiState` with a `MutableStateFlow`.
- Use `composeTestRule.setContent { }` to set the composable under test.
- Assert with `onNodeWithText()`, `onNodeWithTag()`, `onNodeWithContentDescription()`.
- Perform clicks with `.performClick()`.
- Verify ViewModel interactions with `verify { mock.onRetry() }`.

### Paparazzi snapshot test conventions

```kotlin
class PokemonDetailCardSnapshots {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun myComponentSnapshot() {
        paparazzi.snapshot {
            MyComponent()
        }
    }
}
```

**Key rules:**
- Paparazzi tests go in `src/test/java/` (unit test source set).
- Always use `PIXEL_5` device config for consistency.
- Run `./gradlew :app:recordPaparazziDebug` to record golden PNGs.
- Run `./gradlew :app:verifyPaparazziDebug` to verify against golden files.
- Golden PNGs are stored in `src/test/snapshots/images/`.

### Test file location

Tests mirror the source package structure under `src/test/java/` for unit tests and `src/androidTest/java/` for instrumented UI tests:

```
src/
├── test/java/com/mx/beautypoke/
│   ├── data/
│   │   ├── local/mapper/PokemonEntityMapperTest.kt
│   │   └── repository/PokemonRepositoryImplTest.kt
│   └── presentation/
│       ├── screen/PokemonDetailCardSnapshots.kt
│       └── viewmodel/PokemonDetailViewModelTest.kt
└── androidTest/java/com/mx/beautypoke/
    └── presentation/
        ├── component/
        │   ├── PokemonTypeBadgeTest.kt
        │   └── StatBarTest.kt
        └── screen/
            └── PokemonDetailScreenTest.kt
```

## 11. Code Quality

### ktlint (code formatting)

- Uses the `org.jlleitschuh.gradle.ktlint` plugin.
- Follows Kotlin official style guide with 120-char max line length (configured in `.editorconfig`).
- **Always run formatting before committing.** The agent MUST execute `./gradlew ktlintFormat` after making Kotlin code changes.

### detekt (static analysis)

- Uses the `io.gitlab.arturbosch.detekt` plugin.
- Configuration in `config/detekt/detekt.yml`.
- Max 50 issues allowed before build failure.
- **Always run `./gradlew detekt` after making code changes.**

### Agent formatting & lint mandate

After every code change, the agent **MUST** run these commands in order:

```bash
./gradlew ktlintFormat  # Auto-format all Kotlin files
./gradlew ktlintCheck   # Verify formatting is clean
./gradlew detekt        # Run static analysis
```

If `ktlintCheck` or `detekt` fail, fix the issues before committing.

## 12. Build & Run

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedCheck
```

## 13. Commands

- `install`: `./gradlew installDebug`
- `lint`: `./gradlew lint`
- `test`: `./gradlew test`
- `ktlintCheck`: `./gradlew ktlintCheck`
- `ktlintFormat`: `./gradlew ktlintFormat`
- `detekt`: `./gradlew detekt`
- `format`: `./gradlew ktlintFormat && ./gradlew ktlintCheck && ./gradlew detekt`
- `run`: Open project in Android Studio and run the `app` configuration.

## 15. Design System

The project has a small set of reusable Compose components. Every component **must** follow these conventions:

### Component conventions

1. **File location**: One component per file in `presentation/component/`.
2. **Public API**: Default parameters for colors, sizes, and modifiers — composable must work with zero required params beyond `PokemonType` or data.
3. **Dark surface support**: Every component that renders on dark card surfaces (`surface` from `PokemonTheme`) accepts an `onSurfaceColor: Color` parameter.
4. **`@Preview` required**: Every component file must have at least one `@Preview`-annotated private composable showing a representative state. Previews must use `showBackground = true` with a dark surface color to match the app theme.
5. **Paparazzi snapshot test required**: Every new component must have at least one snapshot test in `PokemonDetailCardSnapshots` or a dedicated test class under `src/test/java/`. Record with `./gradlew :app:recordPaparazziDebug`.

### Available components

| Component | File | Has `@Preview` | Has Paparazzi test |
|-----------|------|----------------|--------------------|
| `PokemonTypeBadge` | `PokemonTypeBadge.kt` | Yes (2) | Via `PokemonDetailCard` |
| `StatBar` | `StatBar.kt` | Yes (1) | Via `PokemonDetailCard` |
| `WeaknessPill` | `WeaknessPill.kt` | Yes (1) | Via `PokemonDetailCard` |
| `MetricItem` | `MetricItem.kt` | Yes (2) | Via `PokemonDetailCard` |
| `InfoSection` | `InfoSection.kt` | Yes (1) | Via `PokemonDetailCard` |
| `AbstractPattern` | `AbstractPattern.kt` | Yes (4) | Via `PokemonDetailCard` |
| `PokemonDetailCard` | `PokemonDetailScreen.kt` | Yes (2) | Yes (3 snapshots) |

### Adding a new component

1. Create the file in `presentation/component/`.
2. Write the `@Composable` function with default params, dark surface support.
3. Add at least one `@Preview` function (private, `showBackground = true`, dark background color).
4. Add a Paparazzi test in `PokemonDetailCardSnapshots` (or a new test class).
5. Run `./gradlew :app:recordPaparazziDebug` to generate golden PNGs.
6. Update the Design System table in `README.md`.

## 16. Git Workflow

- Feature branches off `develop` or `main`.
- Conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`.
- **Commit after every change**: each meaningful modification must be committed individually with a clear conventional message.
- **Mandatory pre-commit steps for the agent:**
  1. `./gradlew ktlintFormat` — auto-format
  2. `./gradlew ktlintCheck` — verify no formatting violations
  3. `./gradlew detekt` — verify no static analysis violations
  4. If any step fails, fix the issues and re-run from step 1.
