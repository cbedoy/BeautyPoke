# BeautyPoke 🎀

Android application built with **MVVM + Clean Architecture + SOLID** principles.

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM, Clean Architecture, SOLID, Use Cases |
| Network | Retrofit + OkHttp |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Navigation | Jetpack Navigation Compose |

## Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                  Presentation                    │
│  (Compose UI → ViewModel → UiState Flow)        │
├─────────────────────────────────────────────────┤
│                   Domain                         │
│  (Use Cases → Repository Interfaces → Models)   │
├─────────────────────────────────────────────────┤
│                    Data                          │
│  (Retrofit API → DTOs → Repository Impl)        │
│  (Room DAOs → Entities)                         │
└─────────────────────────────────────────────────┘
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
│   └── repository/     # Repository implementations
├── domain
│   ├── model/          # Domain entities
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic
├── di                  # Hilt modules
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
└── presentation
    ├── navigation/     # NavHost & routes
    ├── theme/          # Compose theme
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
