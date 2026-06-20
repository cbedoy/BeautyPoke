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

## 4. Network Layer (Retrofit)

- Define API interfaces in `data/remote`.
- Use `Result<T>` wrapper or sealed class for API call outcomes.
- Retrofit instance created in DI module; base URL and interceptors configured there.
- DTOs in `data/remote/dto/`, mapped to domain models via mappers in `data/remote/mapper/`.

## 5. Use Cases

Each use case is a single-purpose class with an `operator fun invoke(...)`:

```kotlin
class GetSomethingUseCase(private val repository: SomethingRepository) {
    suspend operator fun invoke(params: Params): Flow<Result<Something>> { ... }
}
```

## 6. ViewModel Conventions

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

## 7. Compose UI

- Screens in `presentation/screen/`.
- Reusable components in `presentation/component/`.
- Theme (colors, typography, shapes) in `presentation/theme/`.
- Navigation graph in `presentation/navigation/BeautyPokeNavHost.kt`.

## 8. Dependency Injection (Hilt)

A DI module per layer is preferred:
- `NetworkModule` — Retrofit, OkHttp
- `RepositoryModule` — binds interfaces to implementations
- `UseCaseModule` / `UseCaseProvider` — wires repository → use case

## 9. Testing

- `domain/` — pure unit tests (JUnit + MockK or Turbine for flows).
- `data/` — unit tests with mocked Retrofit (MockWebServer).
- `presentation/` — ViewModel tests with fake use cases; Compose UI tests.

## 10. Build & Run

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedCheck
```

## 11. Commands

- `install`: `./gradlew installDebug`
- `lint`: `./gradlew lint`
- `test`: `./gradlew test`
- `run`: Open project in Android Studio and run the `app` configuration.

## 12. Git Workflow

- Feature branches off `develop` or `main`.
- Conventional commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`.
- **Commit after every change**: each meaningful modification must be committed individually with a clear conventional message.
