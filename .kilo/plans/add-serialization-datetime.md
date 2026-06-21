# Add kotlinx-serialization & kotlinx-datetime + Document All Dependencies

## Goal

- Add `kotlinx-serialization-json` and `kotlinx-datetime` as declared dependencies in `libs.versions.toml`
- Apply `kotlin-serialization` plugin at project and app level
- Update `README.md` with a comprehensive dependency table
- Update `AGENTS.md` to mention these libraries in the project overview

## Context

These libraries are **available** for future use (e.g., `@Serializable` domain models, date/timestamps for cache freshness). No existing code changes needed — just infrastructure + documentation.

## Files to Modify

### `gradle/libs.versions.toml`
- Add version entries:
  ```toml
  serialization = "1.8.1"
  datetime = "0.6.2"
  ```
- Add library entries:
  ```toml
  kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }
  kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "datetime" }
  ```
- Add plugin entry:
  ```toml
  kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
  ```

### `build.gradle.kts` (project level)
- Add `alias(libs.plugins.kotlin.serialization) apply false`

### `app/build.gradle.kts`
- Add `alias(libs.plugins.kotlin.serialization)` to plugins block
- Add `implementation(libs.kotlinx.serialization.json)` to dependencies
- Add `implementation(libs.kotlinx.datetime)` to dependencies

### `README.md`
- Add `kotlinx-serialization` and `kotlinx-datetime` to the Tech Stack table
- Add a new **"Dependencies"** section with a complete categorized table:

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
| **Testing** | JUnit 4 | 4.13.2 | test |
| | MockK | 1.13.14 | test, androidTest |
| | Turbine | 1.2.0 | test |
| | kotlinx-coroutines-test | 1.10.1 | test, androidTest |
| | Compose UI Test | (via BOM) | androidTest |

### `AGENTS.md`
- Update section 1 (Project Overview) to mention `kotlinx-serialization` and `kotlinx-datetime`
- Update section 5 (Network Layer) to note serialization options

## Commit

```
feat: add kotlinx-serialization and kotlinx-datetime dependencies

- Declare serialization 1.8.1 and datetime 0.6.2 in version catalog
- Apply kotlin-serialization plugin at project and app level
- Add comprehensive dependency tables to README.md
- Update AGENTS.md project overview
```
