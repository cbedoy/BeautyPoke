# BeautyPoke — Project Conventions & Agent Guide

## 1. Project Overview

Android application following **MVVM + Clean Architecture + SOLID** principles.  
Uses **Retrofit** for REST API consumption, **Use Cases** for business logic, **Koin** for DI, and **Jetpack Compose** for UI.

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
- Gson for JSON deserialization.

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
- `presentation/` — ViewModel tests with fake use cases; Compose UI tests.

## 11. Build & Run

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedCheck
```

## 12. Commands

- `install`: `./gradlew installDebug`
- `lint`: `./gradlew lint`
- `test`: `./gradlew test`
- `run`: Open project in Android Studio and run the `app` configuration.

## 13. Git Workflow

- Feature branches off `develop` or `main`.
- Conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`.
- **Commit after every change**: each meaningful modification must be committed individually with a clear conventional message.
