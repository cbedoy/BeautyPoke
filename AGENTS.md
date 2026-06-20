# BeautyPoke — Project Conventions & Agent Guide

## 1. Project Overview

Android application following **MVVM + Clean Architecture + SOLID** principles.  
Uses **Retrofit** for REST API consumption, **Use Cases** for business logic, and **Jetpack Compose** for UI.

## 2. Package Structure (Clean Architecture)

```
com.mx.beautypoke
├── data
│   ├── local          # Room DAOs / local data sources
│   ├── remote         # Retrofit services, DTOs
│   └── repository     # Repository implementations
├── domain
│   ├── model          # Domain entities
│   ├── repository     # Repository interfaces
│   └── usecase        # Business logic use cases
├── di                 # Dependency injection (Hilt/Koin)
└── presentation
    ├── navigation     # NavHost & routes
    ├── theme          # Compose theme
    └── viewmodel      # ViewModels per screen
```

### Dependency rule
- `presentation` → `domain` → `data`  
- `domain` has **zero** dependencies on Android or `data` layer.

## 3. Architecture Rules

### MVVM
- `ViewModel` holds `StateFlow<UiState>` exposed to Compose.
- UI is unidirectional: screen emits events → ViewModel processes → new state emitted.
- No business logic inside Composables.

### Clean Architecture layers

| Layer | Responsibility | Dependencies |
|-------|----------------|--------------|
| `domain` | Entities, repository interfaces, use cases | None |
| `data` | API calls (Retrofit), DB (Room), DTO → domain mapping | domain |
| `presentation` | Compose screens, ViewModels, navigation | domain |
| `di` | Wiring all layers | All layers |

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
  - `GET /pokemon-species/{id}` — color, flavor text, evolution chain

## 5. Network Layer (Retrofit)

- API interfaces defined in `data/remote/api/`.
- DTOs in `data/remote/dto/` with `@SerializedName` for snake_case mapping.
- Retrofit instance: standalone `RetrofitClient` singleton in `data/remote/` (no DI framework yet).
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
| `stats` | `stats[].base_stat` + `stats[].stat.name` | 3-letter stat names mapped to Spanish display names |
| `height` | `pokemon.height` | Decimeters |
| `weight` | `pokemon.weight` | Hectograms |
| `description` | `species.flavor_text_entries` | Prefers English "sword" version; cleans `\f` characters |
| `color` | `species.color.name` | Mapped to `PokemonColor` enum |

## 6. Use Cases

Each use case is a single-purpose class with an `operator fun invoke(...)`:

```kotlin
class GetSomethingUseCase(private val repository: SomethingRepository) {
    suspend operator fun invoke(params: Params): Flow<Result<Something>> { ... }
}
```

## 7. ViewModel Conventions

- One ViewModel per screen or feature group.
- Expose `StateFlow<UiState<T>>` where `UiState` is a sealed interface with `Loading`, `Success`, `Error`.
- Use `viewModelScope` for coroutine lifecycle.

```kotlin
data class SomethingUiState(
    val isLoading: Boolean = false,
    val data: Something? = null,
    val error: String? = null
)
```

## 8. Compose UI

- Screens in `presentation/screen/`.
- Reusable components in `presentation/component/`.
- Theme (colors, typography, shapes) in `presentation/theme/`.
- Navigation graph in `presentation/navigation/BeautyPokeNavHost.kt`.
- **State hoisting**: ViewModel owns the state (`StateFlow`), screen composables receive state + event callbacks as parameters. No `remember` or `mutableStateOf` for business data in composables.
- **UiState** is a `sealed interface` with `Loading`, `Success`, `Error` variants defined in `domain/model/`.
- **Domain models** (`domain/model/`) are pure Kotlin data classes with no Android dependencies.
- **Image loading**: Use Coil's `AsyncImage` for remote images.
- **Pokemon types**: Mapped via `PokemonType` enum with a `color()` extension that resolves to `PokemonColor` for consistent theming.
- **Measurements**: Height in decimeters → meters (`/ 10.0`), weight in hectograms → kilograms (`/ 10.0`).

## 9. Dependency Injection (Hilt)

A DI module per layer is preferred:
- `NetworkModule` — Retrofit, OkHttp
- `RepositoryModule` — binds interfaces to implementations
- `UseCaseModule` / `UseCaseProvider` — wires repository → use case

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
