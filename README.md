# BeautyPoke

Android application built with **MVVM + Clean Architecture + SOLID** principles.

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM, Clean Architecture, SOLID, Use Cases |
| Network | Retrofit + OkHttp |
| DI | Koin |
| Async | Kotlin Coroutines + Flow |
| Navigation | Jetpack Navigation Compose |
| Image Loading | Coil |

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│                                                                  │
│  ┌──────────────┐    ┌──────────────────┐    ┌────────────────┐ │
│  │  Compose UI   │───>│   ViewModel      │───>│   UiState      │ │
│  │  (Screen/     │    │  (StateFlow)     │    │  (Sealed       │ │
│  │   Component)  │<───│                  │<───│   Interface)   │ │
│  └──────────────┘    └────────┬─────────┘    └────────────────┘ │
│                               │                                  │
├───────────────────────────────┼──────────────────────────────────┤
│                        DOMAIN LAYER                              │
│                               │                                  │
│                     ┌─────────▼─────────┐                       │
│                     │    Use Case       │                        │
│                     │  (single invoke)  │                        │
│                     └─────────┬─────────┘                       │
│                               │                                  │
│                     ┌─────────▼─────────┐                       │
│                     │   Repository      │                        │
│                     │   Interface       │                        │
│                     └─────────┬─────────┘                       │
│                               │                                  │
├───────────────────────────────┼──────────────────────────────────┤
│                        DATA LAYER                                │
│                               │                                  │
│              ┌────────────────┼────────────────┐                 │
│              │                │                │                 │
│    ┌─────────▼─────────┐  ┌──▼──────────┐  ┌──▼──────────┐     │
│    │  Remote (Retrofit) │  │   Local     │  │  Mapper     │     │
│    │  API Service      │  │   (Room)    │  │  DTO↔Entity │     │
│    │  DTOs             │  │   DAO       │  │  ↔Domain    │     │
│    └───────────────────┘  │   Entity    │  └─────────────┘     │
│                           └─────────────┘                      │
└──────────────────────────────────────────────────────────────────┘
```

### Data flow

```
User scrolls pager
       │
       ▼
onPageSelected(index) ──> ViewModel
                              │
                              ▼
                    GetPokemonRangeUseCase
                              │
                              ▼
                    PokemonRepositoryImpl
                          │        │
                          ▼        ▼
                    Room (cache)  API (Retrofit)
                          │        │
                          └──┬─────┘
                             ▼
                    Return Result<Pokemon>
                             │
                             ▼
                    ViewModel updates StateFlow
                             │
                             ▼
                    Compose UI re-renders
```

### Dependency direction

```
Presentation ──> Domain <── Data
                      │
                  No Android
                  dependencies
```

### Key decisions

- **Domain layer has zero framework dependencies** — pure Kotlin, no Android imports.
- **Repository interfaces live in domain**, implementations in `data`.
- **Use Cases are single-responsibility** classes with an `invoke` operator.
- **UI state is unidirectional**: Screen → Event → ViewModel → `StateFlow<UiState>` → Screen.
- **Retrofit** for REST; DTOs mapped to domain models with explicit mappers.

## Project Structure

```
com.mx.beautypoke
├── data
│   ├── remote          # Retrofit services, DTOs
│   │   ├── api/
│   │   ├── dto/
│   │   └── mapper/
│   ├── local           # Room database, DAOs, entities
│   │   ├── dao/
│   │   ├── database/
│   │   ├── entity/
│   │   └── mapper/
│   └── repository/     # Repository implementations
├── domain
│   ├── model/          # Domain entities
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic
├── di                  # Koin modules
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   └── ViewModelModule.kt
└── presentation
    ├── navigation/     # NavHost & routes
    ├── theme/          # Compose theme + PokemonTheme palettes
    ├── component/      # Reusable composables
    ├── screen/         # Screen-level composables
    └── viewmodel/      # ViewModels
```

## Getting Started

1. Open the project in **Android Studio Ladybug (2024.3.1)** or newer.
2. Sync Gradle.
3. Run on emulator or device:

```bash
./gradlew installDebug
```

## Commands

```bash
./gradlew assembleDebug   # Build debug APK
./gradlew test            # Run unit tests
./gradlew lint            # Run lint checks
```

## License

MIT
