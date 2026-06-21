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
| Serialization | kotlinx-serialization-json |
| DateTime | kotlinx-datetime |
| Code Quality | ktlint + detekt |

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

## Dependencies

| Category | Library | Version | Scope |
|----------|---------|---------|-------|
| **UI** | Jetpack Compose BOM | 2026.02.01 | implementation |
| | Compose Material 3 | (via BOM) | implementation |
| | Coil Compose | 2.7.0 | implementation |
| | Navigation Compose | 2.8.9 | implementation |
| **Architecture** | Lifecycle ViewModel Compose | 2.10.0 | implementation |
| | Lifecycle Runtime KTX | 2.10.0 | implementation |
| **Network** | Retrofit | 2.11.0 | implementation |
| | Retrofit Gson Converter | 2.11.0 | implementation |
| | OkHttp | 4.12.0 | implementation |
| | Gson | 2.11.0 | implementation |
| **Database** | Room Runtime | 2.7.1 | implementation |
| | Room KTX | 2.7.1 | implementation |
| | Room Compiler | 2.7.1 | annotationProcessor |
| **DI** | Koin Core | 4.0.2 | implementation |
| | Koin Android | 4.0.2 | implementation |
| | Koin AndroidX Compose | 4.0.2 | implementation |
| **Serialization** | kotlinx-serialization-json | 1.8.1 | implementation |
| **DateTime** | kotlinx-datetime | 0.6.2 | implementation |
| **Code Quality** | ktlint Gradle Plugin | 12.2.0 | plugin |
| | detekt Gradle Plugin | 1.23.8 | plugin |
| **Testing** | JUnit 4 | 4.13.2 | test |
| | MockK | 1.13.14 | test, androidTest |
| | Turbine | 1.2.0 | test |
| | kotlinx-coroutines-test | 1.10.1 | test, androidTest |
| | Compose UI Test | (via BOM) | androidTest |

## Screenshots

| Fire Type (Charizard) | Water Type (Blastoise) |
|:---:|:---:|
| ![Charizard detail](screenshots/charizard.png) | ![Blastoise detail](screenshots/blastoise.png) |

> Screenshots are generated by running `PokemonDetailCard` previews in Android Studio.
> To regenerate, open `PokemonDetailScreen.kt`, run the `PokemonDetailCardFirePreview` composable preview, and save the screenshot to `screenshots/`.

## Commands

```bash
./gradlew assembleDebug   # Build debug APK
./gradlew test            # Run unit tests
./gradlew lint            # Run Android lint checks (Android SDK built-in)
./gradlew ktlintCheck     # Run ktlint formatting checks
./gradlew ktlintFormat    # Auto-format Kotlin code
./gradlew detekt          # Run detekt static analysis
./gradlew format          # Alias for ktlintFormat (optional)
```

> **Note:** Always run `./gradlew ktlintFormat && ./gradlew ktlintCheck && ./gradlew detekt` before committing.

## Credits

- App icon and design by **Carlos Bedoy** ([LinkedIn](https://www.linkedin.com/in/carlos-cervantes-bedoy/)).
- Built with [PokéAPI](https://pokeapi.co/) — free and open RESTful API for Pokémon data.
- Inspired by the Pokémon franchise © The Pokémon Company, Nintendo, Game Freak.

## License

MIT
